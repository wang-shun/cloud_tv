package com.letv.portal.service.openstack.impl;

import java.io.IOException;

import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.ExternalGatewayInfo;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.Router;
import org.jclouds.openstack.neutron.v2.domain.Rule;
import org.jclouds.openstack.neutron.v2.domain.RuleDirection;
import org.jclouds.openstack.neutron.v2.domain.RuleEthertype;
import org.jclouds.openstack.neutron.v2.domain.RuleProtocol;
import org.jclouds.openstack.neutron.v2.domain.SecurityGroup;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.extensions.RouterApi;
import org.jclouds.openstack.neutron.v2.extensions.SecurityGroupApi;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;
import com.letv.portal.service.openstack.OpenStackSession;
import com.letv.portal.service.openstack.exception.APINotAvailableException;
import com.letv.portal.service.openstack.exception.OpenStackException;
import com.letv.portal.service.openstack.resource.manager.ImageManager;
import com.letv.portal.service.openstack.resource.manager.NetworkManager;
import com.letv.portal.service.openstack.resource.manager.VMManager;
import com.letv.portal.service.openstack.resource.manager.VolumeManager;
import com.letv.portal.service.openstack.resource.manager.impl.ImageManagerImpl;
import com.letv.portal.service.openstack.resource.manager.impl.NetworkManagerImpl;
import com.letv.portal.service.openstack.resource.manager.impl.VMManagerImpl;
import com.letv.portal.service.openstack.resource.manager.impl.VolumeManagerImpl;

/**
 * Created by zhouxianguang on 2015/6/8.
 */
public class OpenStackSessionImpl implements OpenStackSession {

	@SuppressWarnings("unused")
	private OpenStackServiceGroup openStackServiceGroup;
	@SuppressWarnings("unused")
	private OpenStackConf openStackConf;
	@SuppressWarnings("unused")
	private OpenStackUser openStackUser;

	private ImageManagerImpl imageManager;
	private NetworkManagerImpl networkManager;
	private VMManagerImpl vmManager;
	private VolumeManagerImpl volumeManager;
	// private IdentityManagerImpl identityManager;

	// private Object imageManagerLock;
	// private Object networkManagerLock;
	// private Object vmManagerLock;

	private boolean isClosed;

	public OpenStackSessionImpl(OpenStackServiceGroup openStackServiceGroup,
			OpenStackConf openStackConf, OpenStackUser openStackUser)
			throws OpenStackException {
		this.openStackServiceGroup = openStackServiceGroup;
		this.openStackConf = openStackConf;
		this.openStackUser = openStackUser;

		// this.imageManagerLock = new Object();
		// this.networkManagerLock = new Object();
		// this.vmManagerLock = new Object();

		openStackUser.setPrivateNetworkName(openStackConf
				.getUserPrivateNetworkName());

		// identityManager = new IdentityManagerImpl(openStackServiceGroup,
		// openStackConf, openStackUser);
		imageManager = new ImageManagerImpl(openStackServiceGroup,
				openStackConf, openStackUser);
		networkManager = new NetworkManagerImpl(openStackServiceGroup,
				openStackConf, openStackUser);
		volumeManager = new VolumeManagerImpl(openStackServiceGroup,
				openStackConf, openStackUser);
		vmManager = new VMManagerImpl(openStackServiceGroup, openStackConf,
				openStackUser);

		vmManager.setImageManager(imageManager);
		vmManager.setNetworkManager(networkManager);
		vmManager.setVolumeManager(volumeManager);
		// vmManager.setIdentityManager(identityManager);

		// volumeManager.setIdentityManager(identityManager);

		isClosed = false;

		// if (openStackUser.getFirstLogin()) {
		NeutronApi neutronApi = networkManager.getNeutronApi();
		for (String region : neutronApi.getConfiguredRegions()) {
			NetworkApi networkApi = neutronApi.getNetworkApi(region);

			Network publicNetwork = networkManager.getPublicNetwork(region);
			// for (Network network : networkApi.list().concat().toList()) {
			// if ("__public_network".equals(network.getName())) {
			// publicNetwork = network;
			// break;
			// }
			// }
			if (publicNetwork == null) {
				throw new OpenStackException(
						"can not find public network under region: " + region,
						"后台服务异常");
			}
			openStackUser.setPublicNetworkName(publicNetwork.getName());

			if (openStackUser.getInternalUser()&&!openStackConf.getGlobalSharedNetworkId().isEmpty()) {
				openStackUser.setSharedNetworkName(networkApi.get(
						openStackConf.getGlobalSharedNetworkId()).getName());
			}

			Optional<SecurityGroupApi> securityGroupApiOptional = neutronApi
					.getSecurityGroupApi(region);
			if (!securityGroupApiOptional.isPresent()) {
				throw new APINotAvailableException(SecurityGroupApi.class);
			}
			SecurityGroupApi securityGroupApi = securityGroupApiOptional.get();

			SecurityGroup defaultSecurityGroup = null;
			for (SecurityGroup securityGroup : securityGroupApi
					.listSecurityGroups().concat().toList()) {
				if ("default".equals(securityGroup.getName())) {
					defaultSecurityGroup = securityGroup;
					break;
				}
			}
			if (defaultSecurityGroup == null) {
				defaultSecurityGroup = securityGroupApi
						.create(SecurityGroup.CreateSecurityGroup
								.createBuilder().name("default").build());
			}

			Rule pingRule = null, sshRule = null;
			for (Rule rule : defaultSecurityGroup.getRules()) {
				if (pingRule != null && sshRule != null) {
					break;
				}
				if (pingRule == null) {
					if (rule.getDirection() == RuleDirection.INGRESS
							&& rule.getEthertype() == RuleEthertype.IPV4
							&& rule.getProtocol() == RuleProtocol.ICMP
							&& "0.0.0.0/0".equals(rule.getRemoteIpPrefix())) {
						pingRule = rule;
					}
				}
				if (sshRule == null) {
					if (rule.getDirection() == RuleDirection.INGRESS
							&& rule.getEthertype() == RuleEthertype.IPV4
							&& rule.getProtocol() == RuleProtocol.TCP
							&& "0.0.0.0/0".equals(rule.getRemoteIpPrefix())
							&& rule.getPortRangeMin() == 22
							&& rule.getPortRangeMax() == 22) {
						sshRule = rule;
					}
				}
			}
			if (pingRule == null) {
				pingRule = securityGroupApi.create(Rule.CreateRule
						.createBuilder(RuleDirection.INGRESS,
								defaultSecurityGroup.getId())
						.ethertype(RuleEthertype.IPV4)
						.protocol(RuleProtocol.ICMP)
						.remoteIpPrefix("0.0.0.0/0").portRangeMax(255).portRangeMin(0).build());
			}
			if (sshRule == null) {
				sshRule = securityGroupApi.create(Rule.CreateRule
						.createBuilder(RuleDirection.INGRESS,
								defaultSecurityGroup.getId())
						.ethertype(RuleEthertype.IPV4)
						.protocol(RuleProtocol.TCP).portRangeMin(22)
						.portRangeMax(22).remoteIpPrefix("0.0.0.0/0").build());
			}
		}
		// }
	}

	@Override
	public ImageManager getImageManager() {
		// if (imageManager == null) {
		// synchronized (this.imageManagerLock) {
		// if (imageManager == null) {
		// imageManager = new ImageManagerImpl(endpoint, userId,
		// password);
		// }
		// }
		// }
		return imageManager;
	}

	@Override
	public NetworkManager getNetworkManager() {
		// if (networkManager == null) {
		// synchronized (this.networkManagerLock) {
		// if (networkManager == null) {
		// networkManager = new NetworkManagerImpl(endpoint, userId,
		// password);
		// }
		// }
		// }
		return networkManager;
	}

	@Override
	public VMManager getVMManager() {
		// if (vmManager == null) {
		// synchronized (this.vmManagerLock) {
		// if (vmManager == null) {
		// vmManager = new VMManagerImpl(endpoint, userId, password);
		// }
		// }
		// }
		return vmManager;
	}

	@Override
	public VolumeManager getVolumeManager() {
		return volumeManager;
	}

	@Override
	public boolean isClosed() {
		return isClosed;
	}

	@Override
	public void close() throws IOException {
		isClosed = true;
		// if (identityManager != null) {
		// Closeables.close(identityManager, true);
		// }
		if (imageManager != null) {
			Closeables.close(imageManager, true);
		}
		if (networkManager != null) {
			Closeables.close(networkManager, true);
		}
		if (volumeManager != null) {
			Closeables.close(volumeManager, true);
		}
		if (vmManager != null) {
			Closeables.close(vmManager, true);
		}
	}

	// private NovaApi novaApi;
	// private String tenantName;
	// private String userName;
	// private String password;
	//
	// public OpenStackSessionImpl(String userName) {
	// novaApi = ContextBuilder.newBuilder(provider)
	// .endpoint("http://xxx.xxx.xxx.xxx:5000/v2.0/")
	// .credentials(identity, credential)
	// .modules(modules)
	// .buildApi(NovaApi.class);
	// }
	//
	// void close() {
	//
	// }
	//
	// Set<String> listRegions() {
	//
	// }
	//
}
