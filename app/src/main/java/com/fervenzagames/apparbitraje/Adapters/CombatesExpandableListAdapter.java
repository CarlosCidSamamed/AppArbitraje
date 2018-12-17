package com.fervenzagames.apparbitraje.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.R;


import java.util.List;


public class CombatesExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private List<String> expandableListDetail;


    public CombatesExpandableListAdapter(Context context, List<String> expandableListTitle, List<String> expandableListDetail){
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        if(expandedListText == null){
            Toast.makeText(context, "No se ha podido obtener el String para mostrar en getChildView (CombatesExpandableListAdapter.getChildView)", Toast.LENGTH_SHORT).show();
        } else {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.combates_list_item, null);
            }
            TextView expandedListTextView = (TextView) convertView
                    .findViewById(R.id.list_item_combate_estado);
            expandedListTextView.setText(expandedListText);

        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        if(this.expandableListDetail.size() > 0) {
            return this.expandableListDetail.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }


    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.combates_list_group, null);
        }
        // Estado Combate
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.combate_group_numCombate);
        if(listTitleTextView != null){
            // listTitleTextView.setTypeface(listTitleTextView.getTypeface(), Typeface.BOLD);
            listTitleTextView.setText(listTitle);
        } else {
            Toast.makeText(context, "No se encuentra el elemento de UI correspondiente al TextView (listPosition -- > " + listPosition +  " ) (CombatesExpandableListAdapter.getGroupView)", Toast.LENGTH_SHORT).show();
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

}
