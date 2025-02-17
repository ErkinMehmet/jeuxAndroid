package com.np.brickbreaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private List<StoreItem> items;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(StoreItem item) throws JSONException, IOException;
    }

    public StoreAdapter(List<StoreItem> items, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        StoreItem item = items.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<StoreItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemImage;
        private final TextView itemName;
        private final TextView itemDescription;
        private final TextView itemCost;
        private final Button buyButton;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemCost = itemView.findViewById(R.id.itemCost);
            buyButton = itemView.findViewById(R.id.buyButton);
        }

        public void bind(StoreItem item, OnItemClickListener onItemClickListener) {
            // Load image from assets (if applicable) or use a placeholder
            Context context = itemView.getContext();
            int imageResId = context.getResources().getIdentifier(item.getImage().replace(".png", ""), "drawable", context.getPackageName());
            if (imageResId != 0) {
                itemImage.setImageResource(imageResId);
            } else {
                itemImage.setImageResource(android.R.drawable.ic_menu_gallery); // Placeholder
            }

            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription());
            itemCost.setText("Cost: " + item.getCost() + " points");

            if (item.purchased) {
                buyButton.setEnabled(false);
                buyButton.setText("Purchased");
                buyButton.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                buyButton.setOnClickListener(v -> {
                    // Show a toast for already purchased items
                    Toast.makeText(context, "This item has already been purchased!", Toast.LENGTH_SHORT).show();
                });
            } else {
                buyButton.setEnabled(true);
                buyButton.setText("Buy");
                buyButton.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
                buyButton.setOnClickListener(v -> {
                    try {
                        onItemClickListener.onItemClick(item);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }buyButton.setOnClickListener(v -> {
                try {
                    onItemClickListener.onItemClick(item);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
