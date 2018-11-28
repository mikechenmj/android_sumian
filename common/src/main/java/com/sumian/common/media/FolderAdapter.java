package com.sumian.common.media;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.R;
import com.sumian.common.base.BaseRecyclerAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by haibin
 * on 17/2/27.
 */
class FolderAdapter extends BaseRecyclerAdapter<Folder> {

    FolderAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new FolderViewHolder(mInflater.inflate(R.layout.item_list_folder, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, @NotNull Folder item, int position) {
        FolderViewHolder h = (FolderViewHolder) holder;
        h.tv_name.setText(item.getName());
        h.tv_size.setText(String.format("(%s)", item.getImages().size()));
        mLoader.load(item.getAlbumPath()).into(h.iv_image);
    }

    private static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_name, tv_size;

        FolderViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_folder);
            tv_name = (TextView) itemView.findViewById(R.id.tv_folder_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }
}
