var stompClient = null;
var stompClient2 = null;
var retSubscribe = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function connect() {
    var socket = new SockJS($('#url').val());
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
    });
	
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}


function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function sendMessage() {
    console.log("sending message ...");
    stompClient.send($('#topic').val(), {}, $('#message').val());
}

function subscribe() {
    retSubscribe = stompClient.subscribe($('#topic').val(), function (data) {
        showMessage(data.body);
    });
}

function unsubscribe() {
    retSubscribe.unsubscribe();
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#subscribe" ).click(function() { subscribe(); });
    $( "#unsubscribe" ).click(function() { unsubscribe(); });
    $( "#send" ).click(function() { sendMessage(); });
});

