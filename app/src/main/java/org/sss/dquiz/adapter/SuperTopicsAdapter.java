package org.sss.dquiz.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import org.sss.dquiz.R;
import org.sss.dquiz.activity.MainActivity;
import org.sss.dquiz.model.Topics;

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
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.super_topic_list_view, parent, false);
        }
        Button button = (Button) convertView.findViewById(R.id.topicName);
        button.setText(topics.getSuperTopicVal());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MainActivity().viewDescription(topics.getSuperTopicVal());
            }
        });
        return convertView;


    }
}
