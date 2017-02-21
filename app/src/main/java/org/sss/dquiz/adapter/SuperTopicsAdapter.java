package org.sss.dquiz.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import org.sss.dquiz.R;
import org.sss.dquiz.activity.MainActivity;
import org.sss.dquiz.database.DbObject;
import org.sss.dquiz.model.Topics;
import org.sss.dquiz.service.TopicService;

import java.util.ArrayList;

/**
 * Created by iam on 1/24/17.
 */

public class SuperTopicsAdapter extends ArrayAdapter<Topics> {

    public SuperTopicsAdapter(ArrayList<Topics> topics, Context context){
        super(context, R.layout.super_topic_list_view,topics);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Topics topics = getItem(position);
        int currentTopicId = new TopicService().getCurrentTopic(getContext());
        System.out.println("-----------currentTopicList = " + currentTopicId);
        System.out.println("-------------topicId = " + topics.getTopicId());
        System.out.println("compare--------------"+(currentTopicId==topics.getTopicId()));
        if (currentTopicId<=topics.getTopicId()){
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.super_topic_list_view, parent, false);
            }
        }
        else {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.super_topic_list_view_disable, parent, false);
            }
        }

        final Button button = (Button) convertView.findViewById(R.id.topicName);
        button.setText(topics.getSuperTopicVal());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = view.getTag().toString();
                System.out.println("---------------tag = " + tag);
                if (tag.equals("active-list")){
                    MainActivity.viewDescription(topics.getSuperTopicVal());
                }
                else {
                    MainActivity.viewNotAllowedToast();
                }
            }
        });
        return convertView;
    }

}
