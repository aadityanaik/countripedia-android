package com.halfwitdevs.countripedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableInfoListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> infoGroup;
    private HashMap<String, ArrayList<String>> infoGroupItemMap;

    public ExpandableInfoListAdapter(Context context, List<String> infoGroup,
                                 HashMap<String, ArrayList<String>> infoGroupItemMap) {
        this.context = context;
        this.infoGroup = infoGroup;
        this.infoGroupItemMap = infoGroupItemMap;
    }

    @Override
    public int getGroupCount() {
        return infoGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return infoGroupItemMap.get(infoGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return infoGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return infoGroupItemMap.get(infoGroup.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_info_layout, null);
        }

        TextView infoGrp = convertView.findViewById(R.id.country_info_group);
        infoGrp.setText(groupTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String infoItemText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_info_layout, null);
        }

        TextView infoItem = convertView.findViewById(R.id.country_info_item);
        infoItem.setText(infoItemText);

        return convertView;
    }

    public void update(HashMap<String, ArrayList<String>> newInfoGroupItemMap) {
        infoGroupItemMap = newInfoGroupItemMap;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
