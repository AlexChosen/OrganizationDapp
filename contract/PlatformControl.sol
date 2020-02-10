pragma solidity ^0.5.0;

import "./SafeMath.sol";

contract PlatformControl{
	address private _owner;
	address[] public authorizedGroup;
	mapping (address => bool) public frozenAccount;
	 uint256  public dailyReleaseAmount = 10 ** 6;
	uint256 public largeAmount = 50000;
	
	string private _name;
    string private _symbol;
    uint8 private _decimals;
	uint256 private _totalSupply;
	uint256 private _originSupply;
	
	using SafeMath for uint256;

    mapping (address => uint256) internal _balances;

    mapping (address => mapping (address => uint256)) internal _allowances;

	
	struct Vote {
		string issue;
		string info;
		uint beginTime;
		uint votingTime;
		mapping (address => bool) voter;
		bool valid; //投票中为true，投票完为false
		bool result;
	}
	Vote[] public voteIssues;

	event AddGroup(address group);
	event RemoveGroup(address group);
    event FrozenFunds(address target, bool frozen);
	event LargeAmountTransfer(address from ,address to, uint256 amount);
	event LargeAmountPay(address group, address from ,uint256 amount);
	event Transfer(address indexed from, address indexed to, uint256 value);
	event OwnershipTransferred(address indexed previousOwner, address indexed newOwner);
    event Approval(address indexed owner, address indexed spender, uint256 value);
	event BeginVote(string issue, uint256 voteTime);
	event CalcVote(string issue, bool result);
	
	constructor (string memory name, string memory symbol, uint8 decimals, uint256 totalSupply) public {
        _name = name;
        _symbol = symbol;
        _decimals = decimals;
		_totalSupply = totalSupply;
		_originSupply = _totalSupply;
		_balances[msg.sender] = _totalSupply;
		_owner = msg.sender;
        emit OwnershipTransferred(address(0), _owner);
    }
	
		
	function setLargeAmount(uint256 amount) public onlyOwner {
		largeAmount = amount;
	}	

	function voteBegin(string memory issue, string memory info, uint256 voteType) public onlyOwner returns(bool){
		uint i =0;
		for(i =0 ;i<voteIssues.length; i++){
			if(keccak256(bytes(issue)) == keccak256(bytes(voteIssues[i].issue)) && voteIssues[i].valid == true)
			{
				break;
			}
		}
		require(i==voteIssues.length, "voteBegin: another same name issue is in voting");
	
		uint voteTime = 0;
		if(voteType == 0){
			voteTime = 1 minutes;
		}else if(voteType == 1){
			voteTime = 1 hours;
		}else if(voteType == 2){
			voteTime = 1 days;
		}else if(voteType == 3){
			voteTime = 1 weeks;
		}else {
			return false;
		}

        voteIssues.push(Vote({issue:issue, info:info, beginTime:now, result:false, valid:true, votingTime:voteTime}));
    	Vote storage vote = voteIssues[voteIssues.length-1];
		for(uint j=0;j<authorizedGroup.length;j++){
			//默认所有投票者为同意
			vote.voter[authorizedGroup[j]]=true;
		}
		
		emit BeginVote(issue, voteType);
		return true;
	}
	
	function voteByGroup(string memory issue,bool voteView) public onlyGroup returns(bool){
		uint i = 0;
		for(i =0 ;i<voteIssues.length; i++){
			if(keccak256(bytes(issue)) == keccak256(bytes(voteIssues[i].issue)) && voteIssues[i].valid == true)
			{
				break;
			}
		}
		require(i != voteIssues.length, "voteByGroup: issue is not in voteIssues");
		require(now< (voteIssues[i].beginTime + voteIssues[i].votingTime), "voteByGroup: voting is no longer valid");
		voteIssues[i].voter[msg.sender] = voteView;
		return true; 
	}
	
	function calcVoteResult(string memory issue) public onlyOwner returns(bool){
		uint i =0;
		for(i =0 ;i<voteIssues.length; i++){
		    if(keccak256(bytes(issue)) == keccak256(bytes(voteIssues[i].issue)) && voteIssues[i].valid == true)
			{
				break;
			}
		}
		require(i != voteIssues.length, "voteResult: issue is not valid in voteIssues");
		require(now> (voteIssues[i].beginTime + voteIssues[i].votingTime), "voteResult: voting is still in process");

		Vote storage vote = voteIssues[i];
		uint count=0;
		for(uint j=0;j<authorizedGroup.length;j++){
			if(vote.voter[authorizedGroup[j]] == false){
				count++;
			}
		}
		if(count*2 <= authorizedGroup.length)
		{
			vote.result = true;
		}
		else {
			vote.result = false;
		}
		//_delLastVotedIssueOfSameName(issue);
		emit CalcVote(vote.issue, vote.result);
		
		return true;
	}
	
	/*保留最后一个同名issue*/
	function _delLastVotedIssueOfSameName(string memory issue) internal {
		uint i =0;
		for(i =0 ;i<voteIssues.length; i++){
			if(keccak256(bytes(issue)) == keccak256(bytes(voteIssues[i].issue))  && voteIssues[i].valid == false){
				voteIssues[i] = voteIssues[voteIssues.length-1];
				delete voteIssues[voteIssues.length-1];
				voteIssues.length--;
				break;
			}
		}
	}
	
	function _invalidIssue(string memory issue) internal {
	    uint i =0;
		for(i =0 ;i<voteIssues.length; i++){
			if(keccak256(bytes(issue)) == keccak256(bytes(voteIssues[i].issue))  && voteIssues[i].valid == true){
				voteIssues[i].valid = false;
				break;
			}
		}
	}
	
	
	function _getIssueResult(string memory issue) internal view returns(bool){
		uint i =0;
		for(i =0 ;i<voteIssues.length; i++){
			if(keccak256(bytes(issue)) == keccak256(bytes(voteIssues[i].issue))  && voteIssues[i].valid == true)
			{
				if(voteIssues[i].result==true){
					return true;
				}
			}
		}
		return false;
	}
	

	
	function addAuthorizedGroup(address group) public onlyOwner returns(bool){
	    string memory voteName = "addAuthorizedGroup";
		require(address(group) != address(0), "addAuthorizedGroup: group is the zero address");
		require(_getIssueResult(voteName)==true, "addAuthorizedGroup: issue not accepted by groups");
		authorizedGroup.push(group);
		_invalidIssue(voteName);
		emit AddGroup(group);
		return true;
	}	
		
	function removeAuthorizedGroup(address group) public onlyOwner returns(bool){
	    string memory voteName = "removeAuthorizedGroup";
		require(address(group) != address(0), "removeAuthorizedGroup: group is the zero address");
		uint i=0;
		for(i=0;i<authorizedGroup.length;i++){
			if(authorizedGroup[i] == group){
				break;
			}
		}
		require(i!=authorizedGroup.length, "removeAuthorizedGroup: group is not in authorizedGroup");
		require(_getIssueResult(voteName)==true, "addAuthorizedGroup: issue not accepted by groups");
		
		authorizedGroup[i] = authorizedGroup[authorizedGroup.length-1];
		delete authorizedGroup[authorizedGroup.length-1];
		authorizedGroup.length--;
		_invalidIssue(voteName);
		emit RemoveGroup(group);
		return true;
	}	
	
	modifier onlyGroup(){
		uint i=0;
		for(i=0;i<authorizedGroup.length;i++){
			if(authorizedGroup[i] == msg.sender){
				break;
			}
		}
		require(i!=authorizedGroup.length, "onlyGroup: caller is not in authorizedGroup");
		_;
	}
	
	function freezeAccount(address target, bool freeze) public onlyOwner{
        frozenAccount[target] = freeze;
        emit FrozenFunds(target, freeze);
    }
	
	function releaseDaily(address group) public onlyOwner returns(uint256 ){
		require(address(group) != address(0), "releaseDaily: group is the zero address");
		uint256 amount = dailyReleaseAmount.div(authorizedGroup.length);
		transfer(group, amount);
		emit Transfer(_owner, group, amount);
		return amount;
	}
	
	function transferToUserByGroup(address user, uint256 amount) public onlyGroup{
		require(!frozenAccount[user], ":User is frozen.");
		transferByGroup(user, amount);
		if(amount >= largeAmount){
			emit LargeAmountTransfer(msg.sender, user, amount);
		}
	}
	
	function userPayByGroup(address user, uint256 amount) public onlyGroup{
		require(!frozenAccount[user], ":User is frozen.");
		_burnFrom(user, amount);
		if(amount >= largeAmount){
			emit LargeAmountPay(msg.sender, user, amount);
		}
	}	
	
	
    function name() public view returns (string memory) {
        return _name;
    }


    function symbol() public view returns (string memory) {
        return _symbol;
    }


    function decimals() public view returns (uint8) {
        return _decimals;
    }
	
	function transferByGroup(address recipient, uint256 amount) internal returns(bool){
		_transfer(msg.sender, recipient, amount);
		_approve(recipient, msg.sender, _allowances[recipient][msg.sender].add(amount));
		return true;
	}
	
    function originSupply() public view returns (uint256) {
        return _originSupply;
    }
	
    function totalSupply() public view returns (uint256) {
        return _totalSupply;
    }

    function balanceOf(address account) public view returns (uint256) {
        return _balances[account];
    }

    function allowance(address owner, address spender) public view returns (uint256) {
        return _allowances[owner][spender];
    }

	function transfer(address recipient, uint256 amount) public returns (bool) {
        _transfer(msg.sender, recipient, amount);
        return true;
    }

	/*
    function approve(address spender, uint256 value) public returns (bool) {
        _approve(msg.sender, spender, value);
        return true;
    }


    function transferFrom(address sender, address recipient, uint256 amount) public returns (bool) {
        _transfer(sender, recipient, amount);
        _approve(sender, msg.sender, _allowances[sender][msg.sender].sub(amount));
        return true;
    }


    function increaseAllowance(address spender, uint256 addedValue) public returns (bool) {
        _approve(msg.sender, spender, _allowances[msg.sender][spender].add(addedValue));
        return true;
    }


    function decreaseAllowance(address spender, uint256 subtractedValue) public returns (bool) {
        _approve(msg.sender, spender, _allowances[msg.sender][spender].sub(subtractedValue));
        return true;
    }
	*/

    function _transfer(address sender, address recipient, uint256 amount) internal {
        require(sender != address(0), "ERC20: transfer from the zero address");
        require(recipient != address(0), "ERC20: transfer to the zero address");

        _balances[sender] = _balances[sender].sub(amount);
        _balances[recipient] = _balances[recipient].add(amount);
        emit Transfer(sender, recipient, amount);
    }

	/*
    function _mint(address account, uint256 amount) internal {
        require(account != address(0), "ERC20: mint to the zero address");

        _totalSupply = _totalSupply.add(amount);
        _balances[account] = _balances[account].add(amount);
        emit Transfer(address(0), account, amount);
    }
	*/

	
    function _burn(address account, uint256 value) internal {
        require(account != address(0), "ERC20: burn from the zero address");

        _totalSupply = _totalSupply.sub(value);
        _balances[account] = _balances[account].sub(value);
        emit Transfer(account, address(0), value);
    }
	

    function _approve(address owner, address spender, uint256 value) internal {
        require(owner != address(0), "ERC20: approve from the zero address");
        require(spender != address(0), "ERC20: approve to the zero address");

        _allowances[owner][spender] = value;
        emit Approval(owner, spender, value);
    }


    function _burnFrom(address account, uint256 amount) internal {
        _burn(account, amount);
        _approve(account, msg.sender, _allowances[account][msg.sender].sub(amount));
    }
	
	
	 function owner() public view returns (address) {
        return _owner;
    }


    modifier onlyOwner() {
        require(isOwner(), "Ownable: caller is not the owner");
        _;
    }


    function isOwner() public view returns (bool) {
        return msg.sender == _owner;
    }


    function renounceOwnership() public onlyOwner {
        emit OwnershipTransferred(_owner, address(0));
        _owner = address(0);
    }


    function transferOwnership(address newOwner) public onlyOwner {
        _transferOwnership(newOwner);
    }


    function _transferOwnership(address newOwner) internal {
        require(newOwner != address(0), "Ownable: new owner is the zero address");
        emit OwnershipTransferred(_owner, newOwner);
        _owner = newOwner;
    }
	
}