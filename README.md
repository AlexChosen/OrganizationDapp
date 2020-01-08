# OrganizationDapp
solidty编写的智能合约，实现一个去中心化的组织投票和成员激励的机制。业务逻辑在springboot中实现


## 说明
springboot后端服务通过web3j，与以太坊geth客户端节点交互

合约修改了ERC20接口，目的是实现一个去中心化的自治机构，各机构完全由投票添加，并表决事项。引入token机制，激励各机构下的成员
