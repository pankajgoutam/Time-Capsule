package infiknightians.timecapsule;
import java.text.SimpleDateFormat;
import java.util.*;

public class Priority {

    public ArrayList<Message> sortMessage(List<Message> List)
    {
        PriorityQueue<Message> PQueue;
        ArrayList<Message> newList = new ArrayList<Message>();
        PQueue = new PriorityQueue<Message>(10, new SortByLatestMessage());
        for (Message M : List)
        {
            PQueue.add(M);
        }
        while (!PQueue.isEmpty())
        {
            newList.add(PQueue.poll());
        }
        return  newList;
    }

    class SortByLatestMessage implements Comparator<Message> {
        @Override
        public int compare(Message m1, Message m2) {

            if(m2.getId() > m1.getId())
                return 1;
            else if(m2.getId() < m1.getId())
                return  -1;
            else
                return 0;

        }
    }
}