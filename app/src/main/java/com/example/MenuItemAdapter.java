package com.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.wsrfood.R;

import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<MenuItem> menuItems;

    MenuItemAdapter(Context context, ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public  MenuItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( MenuItemAdapter.ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);

        holder.iconView.setImageResource(menuItem.getIconResource());
        holder.nameView.setText(menuItem.getNameDish());
        holder.priceView.setText(String.valueOf(menuItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconView;
        final TextView nameView, priceView;
        ViewHolder(View view){
            super(view);
            iconView = view.findViewById(R.id.dish_icon);
            nameView = view.findViewById(R.id.name);
            priceView = view.findViewById(R.id.price);
        }
    }
}
