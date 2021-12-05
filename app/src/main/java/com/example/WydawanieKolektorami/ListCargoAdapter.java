package com.example.WydawanieKolektorami;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListCargoAdapter extends RecyclerView.Adapter<ListCargoAdapter.ViewHolder> {
    private Context context;
    ArrayList<Cargo> cargoList;


    public ListCargoAdapter(Context context, ArrayList<Cargo> cargoList) {
        this.context = context;
        this.cargoList = cargoList;

    }

    @NonNull
    @Override
    public ListCargoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cargo_list_recyclerview, parent, false);
        return new ListCargoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.nameCargo.setText(cargoList.get(position).name);
        holder.quantity.setText(cargoList.get(position).quantity+"");
        holder.unit.setText(cargoList.get(position).unit+"");
        holder.eraseCargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cargoList.get(position).quantity > 1) {
                    cargoList.get(position).quantity--;
                    notifyDataSetChanged();
                }
                else {
                    cargoList.remove(position);
                  notifyDataSetChanged();
                }

            }

        });
        holder.eraseCargo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cargoList.remove(position);
                notifyItemRemoved(position);
                return false;
            }
        });

        holder.quantity.addTextChangedListener(new TextWatcher() {
            double afterTextChanged = 0;
            double beforeTextChanged = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged = Double.parseDouble(holder.quantity.getText()+"");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {




            }

            @Override
            public void afterTextChanged(Editable s) {
              /*  afterTextChanged = Double.parseDouble(holder.quantity.getText()+"");
                if (beforeTextChanged < afterTextChanged) {
                    int[] colorDrawables = {Color.WHITE, Color.GREEN, Color.WHITE};
                    ValueAnimator animator = ValueAnimator.ofArgb(colorDrawables);
                    animator.setDuration(1500);
                    animator.addUpdateListener(animation ->
                            holder.linearLayout.setCardBackgroundColor(((int) animator.getAnimatedValue()))
                    );
                    animator.start();*/

                //}

            }
        });
    }

    public int getItemCount() {
        return cargoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameCargo, quantity, unit;
        FloatingActionButton eraseCargo;
        MaterialCardView linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCargo = itemView.findViewById(R.id.textview_name);
            quantity = itemView.findViewById(R.id.textview_quantity);
            eraseCargo = itemView.findViewById(R.id.erase_button);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            unit = itemView.findViewById(R.id.textview_unit);

        }
    }



}
