package org.sss.dquiz.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sss.dquiz.R;
import org.sss.dquiz.activity.TopicsActivity;
import org.sss.dquiz.model.Topics;

import java.util.ArrayList;

/**
 * Created by iam on 1/26/17.
 */

public class TopicsAdapter extends ArrayAdapter<Topics> {

    public TopicsAdapter(ArrayList<Topics> topics, Context context){
        super(context, R.layout.topicinfo_list_view,topics);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Topics topics = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.topicinfo_list_view, parent, false);
        }

        RelativeLayout topicinfoList = (RelativeLayout) convertView.findViewById(R.id.topicinfoList);

        TextView topicId = (TextView) convertView.findViewById(R.id.topicId);
        topicId.setText(topics.getTopicId()+"");

        TextView topicVal = (TextView) convertView.findViewById(R.id.topicVal);
        topicVal.setText(topics.getTopic_val());
        topicinfoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopicsActivity.viewContent(topics.getTopicId(),topics.getTopic_val());
            }
        });
        TextView topicDescription = (TextView) convertView.findViewById(R.id.topicDescription);
        topicDescription.setText(topics.getDescription());
        return convertView;
    }
}
