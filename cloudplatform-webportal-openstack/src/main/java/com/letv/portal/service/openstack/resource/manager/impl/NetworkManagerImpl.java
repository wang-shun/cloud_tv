package com.letv.portal.service.openstack.resource.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.ExternalGatewayInfo;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.neutron.v2.domain.Router;
import org.jclouds.openstack.neutron.v2.domain.Subnet;
import org.jclouds.openstack.neutron.v2.extensions.RouterApi;
import org.jclouds.openstack.neutron.v2.features.NetworkApi;
import org.jclouds.openstack.neutron.v2.features.SubnetApi;

import com.google.common.collect.ImmutableSet;
import com.letv.portal.service.openstack.exception.OpenStackException;
import com.letv.portal.service.openstack.exception.ResourceNotFoundException;
import com.letv.portal.service.openstack.impl.OpenStackConf;
import com.letv.portal.service.openstack.impl.OpenStackUser;
import com.letv.portal.service.openstack.resource.NetworkResource;
import com.letv.portal.service.openstack.resource.SubnetResource;
import com.letv.portal.service.openstack.resource.impl.NetworkResourceImpl;
import com.letv.portal.service.openstack.resource.impl.SubnetResourceImpl;
import com.letv.portal.service.openstack.resource.manager.NetworkManager;

public class NetworkManagerImpl extends AbstractResourceManager<NeutronApi> implements
        NetworkManager {

    /**
	 * 
	 */
	private static final long serialVersionUID = -705565951531564369L;
//	private NeutronApi neutronApi;

	public NetworkManagerImpl() {
	}
	
    public NetworkManagerImpl(OpenStackConf openStackConf, OpenStackUser openStackUser) {
        super( openStackConf, openStackUser);

//        Iterable<Module> modules = ImmutableSet
//                .<Module>of(new SLF4JLoggingModule());
//
//        neutronApi = ContextBuilder.newBuilder("openstack-neutron")
//                .endpoint(openStackConf.getPublicEndpoint())
//                .credentials(openStackUser.getUserId() + ":" + openStackUser.getUserId(), openStackUser.getPassword())
//                .modules(modules).buildApi(NeutronApi.class);
    }

    @Override
    public void close() throws IOException {
//        neutronApi.close();
    }

    @Override
    public Set<String> getRegions() throws OpenStackException {
    	return runWithApi(new ApiRunnable<NeutronApi,Set<String>>() {
			
			@Override
			public Set<String> run(NeutronApi neutronApi) throws Exception {
				return neutronApi.getConfiguredRegions();
			}
		});
    }

    @Override
    public List<NetworkResource> list(final String region)
            throws OpenStackException {
    	return runWithApi(new ApiRunnable<NeutronApi, List<NetworkResource>>() {

			@Override
			public List<NetworkResource> run(NeutronApi neutronApi) throws Exception {
				 checkRegion(region);

			        NetworkApi networkApi = neutronApi.getNetworkApi(region);
			        SubnetApi subnetApi = neutronApi.getSubnetApi(region);

			        List<Network> networks = networkApi.list().concat().toList();
			        List<Subnet> subnets = subnetApi.list().concat().toList();

			        Map<String, NetworkResourceImpl> idToNetwork = new HashMap<String, NetworkResourceImpl>();
			        for (Network network : networks) {
			            idToNetwork.put(network.getId(), new NetworkResourceImpl(region, network, new LinkedList<SubnetResource>()));
			        }

			        for (Subnet subnet : subnets) {
			            idToNetwork.get(subnet.getNetworkId()).getSubnets().add(new SubnetResourceImpl(region, subnet));
			        }

			        List<NetworkResource> networkResources = new ArrayList<NetworkResource>(
			                idToNetwork.size());
			        for (NetworkResourceImpl networkResourceImpl : idToNetwork.values()) {
			            networkResources.add(networkResourceImpl);
			        }

			        return networkResources;
			}
		});
    }

    @Override
    public NetworkResource get(final String region,final String id)
            throws OpenStackException {
    	return runWithApi(new ApiRunnable<NeutronApi, NetworkResource>() {

			@Override
			public NetworkResource run(NeutronApi neutronApi) throws Exception {
				checkRegion(region);

		        NetworkApi networkApi = neutronApi.getNetworkApi(region);
		        Network network = networkApi.get(id);

		        if (network != null) {
		            NetworkResourceImpl networkResourceImpl = new NetworkResourceImpl(region, network, new LinkedList<SubnetResource>());

		            ImmutableSet<String> subnetIds = network.getSubnets();
		            List<Subnet> allSubnets = neutronApi.getSubnetApi(region).list().concat().toList();

		            for (Subnet subnet : allSubnets) {
		                if (subnetIds.contains(subnet.getId())) {
		                    networkResourceImpl.getSubnets().add(new SubnetResourceImpl(region, subnet));
		                }
		            }
		            return networkResourceImpl;
		        } else {
		            throw new ResourceNotFoundException("Network","网络",id);
		        }
			}
    		
		});
    }

//    public NeutronApi getNeutronApi() {
//		return neutronApi;
//	}

    public Network getPublicNetwork(final String region) throws OpenStackException{
    	return runWithApi(new ApiRunnable<NeutronApi, Network>() {

			@Override
			public Network run(NeutronApi neutronApi) throws Exception {
				return neutronApi.getNetworkApi(region).get(openStackConf.getGlobalPublicNetworkId());
			}
    	});
    }

    public Subnet getUserPrivateSubnet(final String region) throws OpenStackException{
    	return runWithApi(new ApiRunnable<NeutronApi, Subnet>() {

			@Override
			public Subnet run(NeutronApi neutronApi) throws Exception {
				SubnetApi subnetApi=neutronApi.getSubnetApi(region);
		        Subnet privateSubnet = null;
		        for (Subnet subnet : subnetApi.list().concat().toList()) {
		            if (openStackConf.getUserPrivateNetworkSubnetName().equals(
		                    subnet.getName())) {
		                privateSubnet = subnet;
		                break;
		            }
		        }
		        return privateSubnet;
			}
		});
    }

    public Network getUserPrivateNetwork(final String region) throws OpenStackException{
		return runWithApi(new ApiRunnable<NeutronApi, Network>() {

			@Override
			public Network run(NeutronApi neutronApi) throws Exception {
				NetworkApi networkApi = neutronApi.getNetworkApi(region);
				Network privateNetwork = null;
				for (Network network : networkApi.list().concat().toList()) {
					if (openStackConf.getUserPrivateNetworkName().equals(
							network.getName())) {
						privateNetwork = network;
						break;
					}
				}
				return privateNetwork;
			}
		});
    }

    public Network getOrCreateUserPrivateNetwork(final String region) throws OpenStackException{
    	return runWithApi(new ApiRunnable<NeutronApi, Network>() {

			@Override
			public Network run(NeutronApi neutronApi) throws Exception {
    	
        NetworkApi networkApi=neutronApi.getNetworkApi(region);

        Network privateNetwork = getUserPrivateNetwork(region);
        if (privateNetwork == null) {
            privateNetwork = networkApi.create(Network.CreateNetwork
                    .createBuilder("")
                    .name(openStackConf.getUserPrivateNetworkName())
                    .build());
        }

        SubnetApi subnetApi = neutronApi.getSubnetApi(region);

        Subnet privateSubnet = getUserPrivateSubnet(region);
        if (privateSubnet == null) {
            privateSubnet = subnetApi
                    .create(Subnet.CreateSubnet
                            .createBuilder(
                                    privateNetwork.getId(),
                                    openStackConf
                                            .getUserPrivateNetworkSubnetCidr())
                            .enableDhcp(true)
                            .name(openStackConf
                                    .getUserPrivateNetworkSubnetName())
                            .ipVersion(4).build());
        }

        return privateNetwork;
        
			}
		});
    }

    public Router getOrCreateUserPrivateRouter(final String region) throws OpenStackException {
    	return runWithApi(new ApiRunnable<NeutronApi, Router>() {

			@Override
			public Router run(NeutronApi neutronApi) throws Exception {
        RouterApi routerApi = neutronApi.getRouterApi(region).get();

        Router privateRouter = null;
        for (Router router : routerApi.list().concat().toList()) {
            if (openStackConf.getUserPrivateRouterName().equals(
                    router.getName())) {
                privateRouter = router;
                break;
            }
        }
        if (privateRouter == null) {
            privateRouter = routerApi.create(Router.CreateRouter
                    .createBuilder()
                    .name(openStackConf.getUserPrivateRouterName())
                    .externalGatewayInfo(ExternalGatewayInfo.builder()// .enableSnat(true)
                            .networkId(getPublicNetwork(region).getId()).build())
                    .build());
            try {
                routerApi.addInterfaceForSubnet(privateRouter.getId(),
                        getUserPrivateSubnet(region).getId());
            } catch (Exception ex) {
                routerApi.delete(privateRouter.getId());
                throw new OpenStackException("后台服务异常", ex);
            }
        }

        // Router router = routerApi.create(Router.CreateRouter
        // .createBuilder().name(openStackConf.getUserPrivateRouterName())
        // .build());
        // .externalGatewayInfo(
        // ExternalGatewayInfo.builder().enableSnat(true)
        // .networkId(publicNetwork.getId())
        // .build())

        return privateRouter;
			}
		});
    }

	@Override
	protected String getProviderOrApi() {
		return "openstack-neutron";
	}

	@Override
	protected Class<NeutronApi> getApiClass() {
		return NeutronApi.class;
	}
}
