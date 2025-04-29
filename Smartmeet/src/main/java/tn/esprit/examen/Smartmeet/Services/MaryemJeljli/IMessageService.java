package tn.esprit.examen.Smartmeet.Services.MaryemJeljli;

import tn.esprit.examen.Smartmeet.entities.MaryemJeljli.Message;

import java.util.List;

public interface IMessageService {
    Message sendMessage(String receiverUsername, String content, List<Integer> documentIds);
    List<Message> getInbox();
    List<Message> getSentMessages();
    void markAsRead(Long messageId);
    long getUnreadMessageCount();
}
