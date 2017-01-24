package org.sss.dquiz.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import org.sss.dquiz.R;
import org.sss.dquiz.model.Topics;

import java.util.ArrayList;

/**
 * Created by iam on 1/24/17.
 */

public class TopicAdapter extends ArrayAdapter<Topics> {

    public TopicAdapter(ArrayList<Topics> topics, Context context){
        super(context, R.layout.topic_list_view,topics);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Topics topics = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.topic_list_view, parent, false);
        }
        Button button = (Button) convertView.findViewById(R.id.topicName);
        button.setText(topics.getSuperTopicVal());
        return convertView;
    }
}
