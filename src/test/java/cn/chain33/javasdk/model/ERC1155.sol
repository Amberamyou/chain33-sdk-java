pragma solidity ^0.8.0;
import "github.com/OpenZeppelin/openzeppelin-contracts/blob/master/contracts/token/ERC1155/ERC1155.sol";

contract AirlineTokens is ERC1155 {
    uint256 public airlineCount;
    
    constructor() public ERC1155("") {
        airlineCount = 0;
    }
    
    function addNewAirline(uint256 initialSupply) external {
        airlineCount++;
        uint256 airlineTokenClassId = airlineCount;

        _mint(msg.sender, airlineTokenClassId, initialSupply, "");        
    }
}