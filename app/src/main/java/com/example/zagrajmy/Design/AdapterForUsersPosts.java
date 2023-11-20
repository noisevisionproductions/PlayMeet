package com.example.zagrajmy.Design;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostsManagement.PostsOfTheGames;
import com.example.zagrajmy.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdapterForUsersPosts extends BaseExpandableListAdapter {
    private final Context context;
    private final List<String> expandableListTitle;
    private final HashMap<String, List<PostCreating>> expandableListDetail;

    public AdapterForUsersPosts(Context context, List<String> expandableListTitle, HashMap<String, List<PostCreating>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.active_parent_items, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.headingParent);
        //  listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final PostCreating expandedListPost = (PostCreating) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.active_child_items, null);
        }

        PostsOfTheGames posts = new PostsOfTheGames();

        TextView numberTextView = convertView.findViewById(R.id.number);
        TextView expandedListTextView = convertView
                .findViewById(R.id.childItems);
        numberTextView.setText("12");
        //expandedListTextView.setText();
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
