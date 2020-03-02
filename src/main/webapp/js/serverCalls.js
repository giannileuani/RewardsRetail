/*
* initial call to populate inventory for sale
*/
document.addEventListener('DOMContentLoaded', function() {
	populateInventoryListings();
}, false);

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
function loadCartResponse(xhr) {
	var respText=xhr.responseText;
	var totItem=xhr.responseXML.getElementsByTagName("ns2:CartStatusResponse");
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
function callALert(msg) {
	alert(msg);
}
