package com.twitterdan.dto.chat.response.message.groupMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupForeignerMessageResponse extends GroupMessageResponseAbstract {
  private final boolean IsForeignerMessage = true;
  private boolean IsMessageSeen = false;
}
