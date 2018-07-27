package com.sumian.common.media.adapter;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.sumian.common.R;
import com.sumian.common.media.ImageGalleryActivity;
import com.sumian.common.media.widget.TweetPicturesPreviewerItemTouchCallback;
import com.sumian.common.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/7/15.
 */
@SuppressWarnings("ALL")
public class MediaSelectImageAdapter extends RecyclerView.Adapter<MediaSelectImageAdapter.TweetSelectImageHolder>
        implements TweetPicturesPreviewerItemTouchCallback.ItemTouchHelperAdapter {
    private final int MAX_SIZE = 9;
    private final int TYPE_NONE = 0;
    private final int TYPE_ADD = 1;
    private final List<Model> mModels = new ArrayList<>();
    private Callback mCallback;

    public MediaSelectImageAdapter(Callback callback) {
        mCallback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE)
            return TYPE_NONE;
        else if (position == size) {
            return TYPE_ADD;
        } else {
            return TYPE_NONE;
        }
    }

    @NonNull
    @Override
    public TweetSelectImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet_publish_selecter, parent, false);
        if (viewType == TYPE_NONE) {
            return new TweetSelectImageHolder(view, new TweetSelectImageHolder.HolderListener() {
                @Override
                public void onDelete(Model model) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        int pos = mModels.indexOf(model);
                        if (pos == -1)
                            return;
                        mModels.remove(pos);
                        if (mModels.size() > 0) {
                            notifyItemRemoved(pos);
                        } else {
                            notifyDataSetChanged();
                            callback.onDeleteCallback();
                        }
                    }
                }

                @Override
                public void onDrag(TweetSelectImageHolder holder) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        // Start a drag whenever the handle view it touched
                        mCallback.onStartDrag(holder);
                    }
                }

                @Override
                public void onClick(Model model) {
//                    ImageGalleryActivity.show(mCallback.getContext(), model.path, false);
                }
            });
        } else {
            return new TweetSelectImageHolder(view, v -> {
                Callback callback = mCallback;
                if (callback != null) {
                    callback.onLoadMoreClick();
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final TweetSelectImageHolder holder, int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE || size != position) {
            Model model = mModels.get(position);
            holder.bind(position, model, mCallback.getImgLoader());
        }
    }

    @Override
    public void onViewRecycled(@NonNull TweetSelectImageHolder holder) {
        mCallback.getImgLoader().clear(holder.mImage);
    }

    @Override
    public int getItemCount() {
        int size = mModels.size();
        if (size == MAX_SIZE) {
            return size;
        } else if (size == 0) {
            return 0;
        } else {
            return size + 1;
        }
    }

    public void clear() {
        mModels.clear();
    }

    public void add(Model model) {
        if (mModels.size() >= MAX_SIZE)
            return;
        mModels.add(model);
    }

    public void add(String path) {
        add(new Model(path));
    }

    public String[] getPaths() {
        int size = mModels.size();
        if (size == 0) {
            return null;
        }
        String[] paths = new String[size];
        int i = 0;
        for (Model model : mModels) {
            paths[i++] = model.path;
        }
        return paths;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Collections.swap(mModels, fromPosition, toPosition);
        if (fromPosition == toPosition)
            return false;

        // Move fromPosition to toPosition
        CollectionUtil.move(mModels, fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mModels.remove(position);
        notifyItemRemoved(position);
    }

    public static class Model {
        public Model(String path) {
            this.path = path;
        }

        public String path;
        public boolean isUpload;
    }

    public interface Callback {
        void onLoadMoreClick();

        RequestManager getImgLoader();

        Context getContext();

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);

        void onDeleteCallback();
    }

    /**
     * TweetSelectImageHolder
     */
    @SuppressWarnings("deprecation")
    static class TweetSelectImageHolder extends RecyclerView.ViewHolder implements TweetPicturesPreviewerItemTouchCallback.ItemTouchHelperViewHolder {
        private ImageView mImage;
        private ImageView mDelete;
        private ImageView mGifMask;
        private HolderListener mListener;

        private TweetSelectImageHolder(View itemView, HolderListener listener) {
            super(itemView);
            mListener = listener;
            mImage = (ImageView) itemView.findViewById(R.id.iv_content);
            mDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            mGifMask = (ImageView) itemView.findViewById(R.id.iv_is_gif);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof MediaSelectImageAdapter.Model) {
                        holderListener.onDelete((MediaSelectImageAdapter.Model) obj);
                    }
                }
            });
            mImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final HolderListener holderListener = mListener;
                    if (holderListener != null) {
                        holderListener.onDrag(TweetSelectImageHolder.this);
                    }
                    return true;
                }
            });
            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = mDelete.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof MediaSelectImageAdapter.Model) {
                        holderListener.onClick((MediaSelectImageAdapter.Model) obj);
                    }
                }
            });
            mImage.setBackgroundColor(0xffdadada);
        }

        private TweetSelectImageHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);

            mImage = (ImageView) itemView.findViewById(R.id.iv_content);
            mDelete = (ImageView) itemView.findViewById(R.id.iv_delete);

            mDelete.setVisibility(View.GONE);
            mImage.setImageResource(R.mipmap.ic_tweet_add);
            mImage.setOnClickListener(clickListener);
            mImage.setBackgroundDrawable(null);
        }

        public void bind(int position, MediaSelectImageAdapter.Model model, RequestManager loader) {
            mDelete.setTag(model);
            // In this we need clear before load

            RequestOptions options = RequestOptions.centerCropTransform().error(R.mipmap.ic_split_graph);
            loader.clear(mImage);
            // Load image
            if (model.path.toLowerCase().endsWith("gif")) {
                loader.load(model.path).apply(options).into(mImage);
                // Show gif mask
                mGifMask.setVisibility(View.VISIBLE);
            } else {
                loader.load(model.path).apply(options).into(mImage);
                mGifMask.setVisibility(View.GONE);
            }
        }


        @Override
        public void onItemSelected() {
            try {
                Vibrator vibrator = (Vibrator) itemView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onItemClear() {

        }

        /**
         * Holder 与Adapter之间的桥梁
         */
        interface HolderListener {
            void onDelete(MediaSelectImageAdapter.Model model);

            void onDrag(TweetSelectImageHolder holder);

            void onClick(MediaSelectImageAdapter.Model model);
        }
    }

}
