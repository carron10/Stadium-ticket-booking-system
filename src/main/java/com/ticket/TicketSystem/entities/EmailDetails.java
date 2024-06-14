package com.ticket.TicketSystem.entities;

// Java Program to Illustrate EmailDetails Class

// Importing required classes
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

	// Class data members
	private String recipient;
	private String msgBody;
	private String subject;
	private String attachment;

    /**
     * @return the recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * @param recipient the recipient to set
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * @return the msgBody
     */
    public String getMsgBody() {
        return msgBody;
    }

    /**
     * @param msgBody the msgBody to set
     */
    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the attachment
     */
    public String getAttachment() {
        return attachment;
    }

    /**
     * @param attachment the attachment to set
     */
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
