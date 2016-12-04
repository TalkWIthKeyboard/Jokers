/**
 * Created by CoderSong on 16/12/3.
 */

$(function () {

    var websocket = null;

    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        var userId = $(".userId").html();
        var roomId = $('.roomId').html();
        websocket = new WebSocket("ws://localhost:8080/RoomWebsocket/" + userId + "/" + roomId);
    }
    else {
        alert('Not support websocket')
    }

    // send点击函数
    $('#send').click(function () {
        joinRoom(websocket);
    });

    // close点击函数
    $('#close').click(function () {
        closeWebSocket();
    });

    //连接发生错误的回调方法
    websocket.onerror = function () {
        setMessageInnerHTML("error");
    };

    //连接成功建立的回调方法
    websocket.onopen = function (event) {
        setMessageInnerHTML("open");
    };

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        if (event.data == "success") {
            window.location.href = "/playTest";
        } else {
            setMessageInnerHTML(event.data);
        }
    };

    //连接关闭的回调方法
    websocket.onclose = function () {
        setMessageInnerHTML("close");
    };

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        websocket.close();
    };
});

//将消息显示在网页上
function setMessageInnerHTML(innerHTML) {
    document.getElementById('message').innerHTML += innerHTML + '<br/>';
}

//关闭连接
function closeWebSocket() {
    websocket.close();
}

//加入房间并在回调向websocket通讯
function joinRoom(websocket) {
    var roomId = $('#text').val();
    var password = $('#password').val();
    var url = "/rooms/?id=" + roomId + '&key=' + password;
    $.ajax({
        url: url,
        type: "GET",
        success: function (data) {
            console.log(data);
            if (data['errorNum'] == 200) {
                websocket.send("enterRoom " + roomId);
            }
        }
    })
}