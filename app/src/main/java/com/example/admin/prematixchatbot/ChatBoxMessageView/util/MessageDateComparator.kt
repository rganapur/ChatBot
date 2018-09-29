package com.example.admin.prematixchatbot.ChatBoxMessageView.util;

import com.example.admin.prematixchatbot.ChatBoxMessageView.model.Message
import java.util.*

class MessageDateComparator : Comparator<Message> {
    override fun compare(first: Message, second: Message): Int {
        if (first.sendTime.before(second.sendTime)) {
            return -1
        }
        return if (first.sendTime.after(second.sendTime)) {
            1
        } else 0
    }
}
