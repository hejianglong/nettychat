package com.chat.androidclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.chat.androidclient.R;
import com.chat.androidclient.mvvm.model.Friend;
import com.chat.androidclient.mvvm.model.Group;
import com.chat.androidclient.mvvm.view.activity.ChatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lps on 2018/12/29 16:05.
 */
public class FriendAdapter extends BaseExpandableListAdapter {
    List<Group> friendList = new ArrayList<>();
    private Context mContext;

    public FriendAdapter(Context contx) {
        mContext = contx;
    }

    @Override
    public int getGroupCount() {
        return friendList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return friendList.get(groupPosition).getMFriendList().size();
    }

    @Override
    public Group getGroup(int groupPosition) {
        return friendList.get(groupPosition);
    }

    @Override
    public Friend getChild(int groupPosition, int childPosition) {
        return friendList.get(groupPosition).getMFriendList().get(childPosition);
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupVH vh = null;
        if (convertView == null) {
            vh = new GroupVH();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_group, null);
            vh.name = convertView.findViewById(R.id.groupname);
            vh.online = convertView.findViewById(R.id.grouponline);
            convertView.setTag(vh);
        } else {
            vh = (GroupVH) convertView.getTag();
        }
        vh.name.setText(getGroup(groupPosition).getName());
        vh.online.setText("0/0");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildVH vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, null);
            vh = new ChildVH();
            convertView.setTag(vh);
        } else {
            vh = (ChildVH) convertView.getTag();
        }
        convertView.setOnClickListener(v ->
           ChatActivity.startActivity(parent.getContext(),getChild(groupPosition,childPosition).getUserId())
        );
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    public void setData(@NotNull List<Group> datas) {
        friendList.clear();
        friendList.addAll(datas);
        notifyDataSetChanged();
    }

    class GroupVH {
        TextView name;
        TextView online;
    }

    class ChildVH {

    }
}
