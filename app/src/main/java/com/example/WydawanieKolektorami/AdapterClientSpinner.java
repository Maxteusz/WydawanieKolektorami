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

public class AdapterClientSpinner extends ArrayAdapter<Client> {
    private Context context;
    private ArrayList<Client> clientsList;
    private ListFilter listFilter = new ListFilter();
    private ArrayList<Client> dataListAllItems;

    public AdapterClientSpinner (Context context, ArrayList<Client> mClientsList){
        super(context, 0, mClientsList);
        clientsList = new ArrayList<>();
        this.clientsList = mClientsList;
        this.context = context;


    }
    @Override
    public int getCount() {
        return clientsList.size();
    }

    @Override
    public Client getItem(int position) {

        return clientsList.get(position);
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
                    dataListAllItems = new ArrayList<Client>(clientsList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<Client> matchValues = new ArrayList<Client>();

                for (Client dataItem : clientsList) {
                    if (dataItem.getName().toLowerCase().startsWith(searchStrLowerCase)) {
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
                clientsList = (ArrayList<Client>) results.values;
            } else {
                clientsList = null;
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

        Client currentClient = clientsList.get(position);
        TextView name = (TextView) listItem.findViewById(R.id.textview_name);
        name.setText(currentClient.getName());
        return listItem;
    }
}
