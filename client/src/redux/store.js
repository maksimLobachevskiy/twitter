import thunk from "redux-thunk";
import {composeWithDevTools} from "redux-devtools-extension";
import {Client} from "@stomp/stompjs";

import api from "@service/API";
import {getTokens, setHeaderAuthorization} from "@utils";
import {interceptor} from "@service/API";
import {authUserSocketSubscribe, getAuthUser} from "./user/action";

import tweetReducer from "./tweet/reducer";
import authReducer from "./auth/reducer";
import userReducer from "./user/reducer";
import dialogReducer from "./dialog/reducer";

import chatReducer from "./chat/reducer";
import messagesReducer from "./chat/message/reducer";

const {applyMiddleware, combineReducers, createStore} = require("redux");

const reducer = combineReducers({
  chat: chatReducer,
  message: messagesReducer,
  tweet: tweetReducer,
  auth: authReducer,
  user: userReducer,
  dialog: dialogReducer,
})

const stompClient = (onConnect) => {
  const client = new Client({
    brokerURL: process.env.REACT_APP_API_BROKER_URL,
    connectHeaders: {
      login: 'user',
      passcode: 'password',
    },
    debug: function (str) {
      // console.log(str);
    },
    reconnectDelay: 5000,
    onConnect,

  });
  api.client = client;

  client.activate();
  return client;
}

export default () => {
  const {accessToken, tokenType} = getTokens();
  const store = createStore(
    reducer,
    composeWithDevTools(applyMiddleware(thunk))
  );
  interceptor(store);

  if (accessToken) {
    setHeaderAuthorization(accessToken, tokenType);
    store.dispatch(getAuthUser())
      .then((data) => {
        api.client = stompClient(() => {
          store.dispatch(authUserSocketSubscribe());
        });
      })
  }

  return store;
}
