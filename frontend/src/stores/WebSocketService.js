import SockJS from "sockjs-client";
import Stomp from "webstomp-client";

let stompClient = null;
let currentSubscription = null;

const WebSocketService = {
  connect(endpoint, headers, onMessage) {
    const socket = new SockJS(endpoint);
    stompClient = Stomp.over(socket);

    stompClient.connect(headers, () => {
      console.log("WebSocket connected");
    });

    // 메시지 구독
    if (onMessage) {
      currentSubscription = stompClient.subscribe("/sub/chat", (message) => {
        onMessage(JSON.parse(message.body));
      });
    }
  },

  sendMessage(destination, message) {
    if (stompClient && stompClient.connected) {
      stompClient.send(destination, JSON.stringify(message), {});
    } else {
      console.error("WebSocket is not connected.");
    }
  },

  disconnect() {
    if (stompClient) {
      if (currentSubscription) currentSubscription.unsubscribe();
      stompClient.disconnect();
    }
  },
};

export default WebSocketService;
