import {createActions} from '../utils';
import api, {URLS} from "../../services/API";
import {ACTIONS as AUTH_ACTIONS} from '../auth/action';
import {ACTIONS as CHAT_ACTIONS} from "../chat/action";
import {ACTIONS as MESSAGE_ACTIONS} from "../chat/message/action";
import NOTIFICATION_ACTIONS_Cust, {ACTIONS as NOTIFICATION_ACTIONS} from "../notification/action";
import destinations from '../../subscriptions.js';
import NOTIFICATIONS_ACTIONS from '@redux/notification/action';

const actions = createActions(
  {
    actions: ['UPDATE_COUNT_UNREAD_MESSAGES', 'RESET_DATA'],
    async: ['GET_AUTH_USER'],
  },
  {
    prefix: "user",
  }
);

export const ACTIONS = {
  ...actions.actions,
  ...actions.async,
}

export const getAuthUser = () => async (dispatch) => {
  try {
    dispatch(ACTIONS.getAuthUser.request());
    const data = await api.get(URLS.USERS.ROOT);
    dispatch(ACTIONS.getAuthUser.success(data));
    dispatch(NOTIFICATIONS_ACTIONS.getNotifications());

  } catch (e) {
    console.log(e);
    dispatch(ACTIONS.getAuthUser.fail(e));
    dispatch(AUTH_ACTIONS.authorize.fail());
  }
}

export const authUserSocketSubscribe = () => async (dispatch, getState) => {
  try {
    const {user: {authUser}} = getState();

    /*** блок коллбэков stomp-Notifications ***/
    authUser?.id && api.client.subscribe(`${destinations.genNotificationsDest}${authUser.id}`, (json) => {
      if (json.body) {
        const message = JSON.parse(json.body);
        dispatch(NOTIFICATION_ACTIONS_Cust.storeNotification(message));

      }
    });
/*** конец блока коллбэков stomp-Notifications ***/



  } catch (err) {
    console.log('chatSubscribes error - ', err);
  }
}
