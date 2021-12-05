package com.example.WydawanieKolektorami;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AdapterWarehouseSpinner extends ArrayAdapter<Warehouse> {

    private Context context;
    private ArrayList<Warehouse> warehousesList;
    private ListFilter listFilter = new ListFilter();
    private ArrayList<Warehouse> dataListAllItems;

    public AdapterWarehouseSpinner (Context context, ArrayList<Warehouse> mWarehousesList){
        super(context, 0, mWarehousesList);
        warehousesList = new ArrayList<>();
        this.warehousesList = mWarehousesList;
        this.context = context;


    }
    @Override
    public int getCount() {
        return warehousesList.size();
    }

    @Override
    public Warehouse getItem(int position) {

        return warehousesList.get(position);
    }

   @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<Warehouse>(warehousesList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<Warehouse> matchValues = new ArrayList<Warehouse>();

                for (Warehouse dataItem : warehousesList) {
                    if (dataItem.name.toLowerCase().startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                warehousesList = (ArrayList<Warehouse>)results.values;
            } else {
                warehousesList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.spinner_layout,parent,false);

        Warehouse currentMovie = warehousesList.get(position);
        TextView name = (TextView) listItem.findViewById(R.id.textview_name);
        name.setText(currentMovie.name);
        return listItem;
    }

}

