pragma solidity ^0.6;
contract MyStore {
    uint value;
    constructor() public{
        value=9999999;
    }
    function set(uint x) public {
        value = x;
    }

    function get() public view returns (uint){
        return value;
    }
}
