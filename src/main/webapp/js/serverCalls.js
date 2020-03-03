/*
* initial call to populate inventory for sale
*/
document.addEventListener('DOMContentLoaded', function() {
//	populateInventoryListings();
	/*
	 * Switch to using a Restful api call
	 */
	populateInventoryListingsREST();
}, false);

/*
*Quick'n'dirty soap object creation
*/
var inventoryReq=
	"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ret=\"http://digiwack.com/retailReward\">"+
"<soapenv:Header/>"+
"<soapenv:Body>"+
   "<ret:InventoryRequest/>"+
"</soapenv:Body>"+
"</soapenv:Envelope>";

function populateInventoryListings() {
	var xhr=new XMLHttpRequest();
	xhr.onload=function() {
		if (xhr.status===200) {
			/*
			*We get back a list of items in the inventory in a SOAP response
			*/
			var elems=xhr.responseXML.getElementsByTagName("ns2:AnItem");
			var respText=xhr.responseText;
			var theTable="<table border='1'>";
			for (var i=0; i<elems.length; i++) {
				theTable+="<tr><td>";
				theTable+=elems[i].getAttribute("itemName");
				theTable+="</td><td>";
				theTable+="$"+elems[i].getAttribute("itemCost");
				theTable+="</td><td><button onClick=\"addItemForCustomer('";
				theTable+=elems[i].getAttribute("itemName");
				theTable+="')\">Add to cart</button>";
				theTable+="</td><td><button onClick=\"addItemForCustomerREST('";
				theTable+=elems[i].getAttribute("itemName");
				theTable+="')\">Add to cart REST</button>";
				theTable+="</td></tr>";
			}
			theTable+="</table>";
			var inventoryListingsDiv = document.getElementById("inventoryListingsDiv");
			inventoryListingsDiv.innerHTML=theTable;
			
		}
	}
	xhr.open("POST", "service/retaleWIZZ.wsdl");
	xhr.setRequestHeader("Content-type", "text/xml");
	xhr.send(inventoryReq);
}
/*
 * Similar to populateInventoryListings, but its making a GET call to a url to get
 * its results
 */
function populateInventoryListingsREST() {
	var xhr=new XMLHttpRequest();
	xhr.onload=function() {
		if (xhr.status===200) {
			/*
			*We get back a list of items in the inventory in a JSON object
			*/
			var inventory=JSON.parse(xhr.responseText);
			var theTable="<table border='1'>";
			for (var i=0; i<inventory.count; i++) {
				theTable+="<tr><td>";
				theTable+=inventory.items[i].name;
				theTable+="</td><td>";
				theTable+="$"+inventory.items[i].price;
				theTable+="</td><td><button onClick=\"addItemForCustomer('";
				theTable+=inventory.items[i].name;
				theTable+="')\">Add to cart</button>";
				theTable+="</td><td><button onClick=\"addItemForCustomerREST('";
				theTable+=inventory.items[i].name;
				theTable+="')\">Add to cart REST</button>";
				theTable+="</td></tr>";
			}
			theTable+="</table>";
			var inventoryListingsDiv = document.getElementById("inventoryListingsDiv");
			inventoryListingsDiv.innerHTML=theTable;
		}
	}
	xhr.open("GET", "inventory");
	xhr.send(null);
}
/*
*Quick'n'dirty SOAP request for a user's cart
*/
var cartReq1 = 
	"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ret=\"http://digiwack.com/retailReward\">"+
"<soapenv:Header/><soapenv:Body><ret:CartStatusRequest>"+
      "<ret:Customer customerName=\"";
var cartReq2="\" customerPhone=\"";
var cartReq3="\"/></ret:CartStatusRequest></soapenv:Body></soapenv:Envelope>";

function lookupCustomer() {
	var custName=document.getElementById("custNameFld").value;
	var custPhone=document.getElementById("custPhoneFld").value;
	var cartReq="";
	if (custName.length>0 && custPhone.length>0) {
		cartReq=cartReq1+custName+cartReq2+custPhone+cartReq3;
		var xhr=new XMLHttpRequest();
		xhr.onload=function() {
			if (xhr.status===200) {
				/*
				*parse shopping cart response
				*/
				loadCartResponse(xhr);
			}
		}
		xhr.open("POST", "service/retaleWIZZ.wsdl");
		xhr.setRequestHeader("Content-type", "text/xml");
		xhr.send(cartReq);
	} else {
		alert("need a customer name and phone number");
	}
}
function lookupCustomerREST() {
	var custName=document.getElementById("custNameFld").value;
	var custPhone=document.getElementById("custPhoneFld").value;
	var cartReq="";
	if (custName.length>0 && custPhone.length>0) {
		var xhr=new XMLHttpRequest();
		xhr.onload=function() {
			if (xhr.status===200) {
				/*
				*parse shopping cart response
				*/
				loadCartResponseREST(xhr);
			}
		}
		/*
		 * Call restful api
		 */
		xhr.open("GET", "shoppingCart?customerName="+custName+"&customerPhone="+custPhone);
//		xhr.setRequestHeader("Content-type", "text/xml");
		xhr.send(cartReq);
	} else {
		alert("need a customer name and phone number");
	}
}
/*
*Quick'n'dirty SOAP request to add item to shopping cart
*/
var addItemReq1=
	"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ret=\"http://digiwack.com/retailReward\">"+
   "<soapenv:Header/><soapenv:Body><ret:AddItemRequest>"+
   "<ret:Customer customerName=\"";
var addItemReq2="\" customerPhone=\"";
var addItemReq3="\"/><ret:ItemName>";
var addItemReq4="</ret:ItemName></ret:AddItemRequest></soapenv:Body></soapenv:Envelope>";

function addItemForCustomer(itm) {
	var custName=document.getElementById("custNameFld").value;
	var custPhone=document.getElementById("custPhoneFld").value;
	var addItemReq="";
	if (custName.length>0 && custPhone.length>0) {
		addItemReq=addItemReq1+custName+addItemReq2+custPhone+addItemReq3+itm+addItemReq4;
		var xhr=new XMLHttpRequest();
		xhr.onload=function() {
			if (xhr.status===200) {
				/*
				*parse shopping cart response
				*/
				loadCartResponse(xhr);
			}
		}
		xhr.open("POST", "service/retaleWIZZ.wsdl");
		xhr.setRequestHeader("Content-type", "text/xml");
		xhr.send(addItemReq);
	} else {
		alert("need a customer name and phone number");
	}
}
function addItemForCustomerREST(itm) {
	var custName=document.getElementById("custNameFld").value;
	var custPhone=document.getElementById("custPhoneFld").value;
	if (custName.length>0 && custPhone.length>0) {
		var xhr=new XMLHttpRequest();
		xhr.onload=function() {
			if (xhr.status===200) {
				/*
				*parse shopping cart response
				*/
				loadCartResponseREST(xhr);
			}
		}
		/*
		 * Call restful api
		 */
		xhr.open("GET", "shoppingCartAdd?customerName="+custName+"&customerPhone="+custPhone+"&itemName="+itm);
//		xhr.setRequestHeader("Content-type", "text/xml");
		xhr.send(null);
	} else {
		alert("need a customer name and phone number");
	}
}
/*
*Quick'n'dirty SOAP request to remove item from shopping cart
*/
var remItemReq1=
	"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ret=\"http://digiwack.com/retailReward\">"+
   "<soapenv:Header/><soapenv:Body><ret:RemoveItemRequest>"+
   "<ret:Customer customerName=\"";
var remItemReq2="\" customerPhone=\"";
var remItemReq3="\"/><ret:ItemName>";
var remItemReq4="</ret:ItemName></ret:RemoveItemRequest></soapenv:Body></soapenv:Envelope>";

function removeItemForCustomer(itm) {
	var custName=document.getElementById("custNameFld").value;
	var custPhone=document.getElementById("custPhoneFld").value;
	var remItemReq="";
	if (custName.length>0 && custPhone.length>0) {
		remItemReq=remItemReq1+custName+remItemReq2+custPhone+remItemReq3+itm+remItemReq4;
		var xhr=new XMLHttpRequest();
		xhr.onload=function() {
			if (xhr.status===200) {
				/*
				*parse shopping cart response
				*/
				loadCartResponse(xhr);
			}
		}
		xhr.open("POST", "service/retaleWIZZ.wsdl");
		xhr.setRequestHeader("Content-type", "text/xml");
		xhr.send(remItemReq);
	} else {
		alert("need a customer name and phone number");
	}
}
function removeItemForCustomerREST(itm) {
	var custName=document.getElementById("custNameFld").value;
	var custPhone=document.getElementById("custPhoneFld").value;
	if (custName.length>0 && custPhone.length>0) {
		var xhr=new XMLHttpRequest();
		xhr.onload=function() {
			if (xhr.status===200) {
				/*
				*parse shopping cart response
				*/
				loadCartResponseREST(xhr);
			}
		}
		/*
		 * Call restful api
		 */
		xhr.open("GET", "shoppingCartRemove?customerName="+custName+"&customerPhone="+custPhone+"&itemName="+itm);
//		xhr.setRequestHeader("Content-type", "text/xml");
		xhr.send(null);
	} else {
		alert("need a customer name and phone number");
	}
}
function loadCartResponse(xhr) {
	var respText=xhr.responseText;
	/*
	 * Total summary element with item count, total cost, reward points
	 */
	var totItem=xhr.responseXML.getElementsByTagName("ns2:CartStatusResponse");
	/*
	 * Individual items and their counts in the shopping cart
	 */
	var cItems = xhr.responseXML.getElementsByTagName("ns2:CartItem");
	var theTable="<table border='1'>";
	for (var i=0; i<cItems.length; i++) {
		theTable+="<tr><td>";
		theTable+=cItems[i].getAttribute("itemName");
		theTable+="</td><td>";
		theTable+=cItems[i].getAttribute("itemCount");
		theTable+="</td><td>";
		theTable+="$"+cItems[i].getAttribute("itemCost");
		theTable+="</td><td><button onClick=\"removeItemForCustomer('";
		theTable+=cItems[i].getAttribute("itemName")
		theTable+="')\">Remove from cart</button>";
		theTable+="</td></tr>";
	}
	theTable+="<tr><td>Totals</td><td>";
	theTable+=totItem[0].getAttribute("ItemCount");
	theTable+="</td><td>";
	theTable+="$"+totItem[0].getAttribute("TotalCost");
	theTable+="</td><td/></tr>";
	theTable+="<tr><td>Reward Points:</td><td colspan=\"3\">";
	theTable+=totItem[0].getAttribute("RewardPoints");
	theTable+="</td></tr></table>";
	var inventoryListingsDiv = document.getElementById("cartListingsDiv");
	inventoryListingsDiv.innerHTML=theTable;
}
/*
 * I can see the appeal in parsing a JSON object instead of XML.
 * However, there is an *.xsd file that is used as the guideline for how the requests'n'responses
 * are to be generated, and will be used to create the Wsdl that defines the webapp's interface
 * for external clients to use.
 * 
 * JSON objects being tossed back'n'forth seem to require... far more meetings between the webapp
 * team and client teams to get the api flow working.  This strategy may seem ideal, where everyone
 * progresses or not at all, but it also takes time away from webapp teams forward moving development time.
 * 
 * I wonder if anyone will ever read this far?
 * 
 * If you do, just send an email to gianni.leuani@gmail.com stating that you read the comment
 * for: RewardsRetail->serverCalls.js->loadCartResponseREST
 *  
 */
function loadCartResponseREST(xhr) {
	var cart=JSON.parse(xhr.responseText);
	var theTable="<table border='1'>";
	for (var i=0; i<cart.count; i++) {
		theTable+="<tr><td>";
		theTable+=cart.items[i].name;
		theTable+="</td><td>";
		theTable+=cart.items[i].count;
		theTable+="</td><td>";
		theTable+="$"+cart.items[i].cost;
		theTable+="</td><td><button onClick=\"removeItemForCustomer('";
		theTable+=cart.items[i].name;
		theTable+="')\">Remove from cart</button>";
		theTable+="</td><td><button onClick=\"removeItemForCustomerREST('";
		theTable+=cart.items[i].name;
		theTable+="')\">Remove from cart REST</button>";
		theTable+="</td></tr>";
	}
	theTable+="<tr><td>Totals</td><td>";
	theTable+=cart.totalItemCount;
	theTable+="</td><td>";
	theTable+="$"+cart.totalCost;
	theTable+="</td><td/></tr>";
	theTable+="<tr><td>Reward Points:</td><td colspan=\"3\">";
	theTable+=cart.rewards;
	theTable+="</td></tr></table>";
	var inventoryListingsDiv = document.getElementById("cartListingsDiv");
	inventoryListingsDiv.innerHTML=theTable;
}
/*
 * Leftover testing
 */
function callALert(msg) {
	alert(msg);
}
