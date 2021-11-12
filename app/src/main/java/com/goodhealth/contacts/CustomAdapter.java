package com.goodhealth.contacts;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Parsania Hardik on 11-May-17.
 */
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ContactModel> contactModelArrayList;
    private ArrayList<String> mutualPhones;
    public CustomAdapter(Context context, ArrayList<ContactModel> contactModelArrayList,  ArrayList<String> mutualPhones) {

        this.context = context;
        this.contactModelArrayList = contactModelArrayList;
        this.mutualPhones = mutualPhones;
    }

    @Override
    public int getViewTypeCount() {
        return getCount() +1;
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return contactModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.tvname = (TextView) convertView.findViewById(R.id.name);
            holder.tvnumber = (TextView) convertView.findViewById(R.id.number);

//            tvnumber = (TextView) convertView.findViewById(R.id.phoneArray);


            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvname.setText(contactModelArrayList.get(position).getName());
        holder.tvnumber.setText(contactModelArrayList.get(position).getNumber());

        Log.i(">> Tag >>", "Mutual Phones Length " +  mutualPhones.size() );

        if(findCommonElements(contactModelArrayList.get(position).getPhoneArray(), mutualPhones)) {
            holder.tvnumber.setTextColor(Color.parseColor("#0000FF"));
        }
        return convertView;
    }

    public static boolean findCommonElements(String[] arr1,
                                             ArrayList<String> arr2)
    {

        System.out.println(arr2);
        // create hashsets
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        // Adding elements from array1
        for (String i : arr1) {
            set1.add(i);
        }

        // Adding elements from array2
        for (String i : arr2) {
            set2.add(i);
        }

        // use retainAll() method to
        // find common elements
        set1.retainAll(set2);
        System.out.println("Common elements- " + set1);

        if (set1.toArray().length > 0) {
            return true;
        } else{
            return false;
        }
    }

    private class ViewHolder {

        protected TextView tvname, tvnumber;

    }



}