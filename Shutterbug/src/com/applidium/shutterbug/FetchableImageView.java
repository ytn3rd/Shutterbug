package com.applidium.shutterbug;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.applidium.shutterbug.cache.ImageCache;
import com.applidium.shutterbug.utils.ShutterbugManager;
import com.applidium.shutterbug.utils.ShutterbugManager.ShutterbugManagerListener;

public class FetchableImageView extends ImageView implements ShutterbugManagerListener {
    public interface FetchableImageViewListener {
        void onImageFetched(Bitmap bitmap, String url);

        void onImageFailure(String url);
    }

    private FetchableImageViewListener mListener;

    public FetchableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FetchableImageViewListener getListener() {
        return mListener;
    }

    public void setListener(FetchableImageViewListener listener) {
        mListener = listener;
    }

    public void setImage(String url) {
        setImage(url, new ColorDrawable(getContext().getResources().getColor(R.color.transparent)));
    }
    
    public void setImage(String url, Boolean refreshCached) {
        setImage(url, new ColorDrawable(getContext().getResources().getColor(R.color.transparent)), refreshCached);
    }

    public void setImage(String url, int desiredHeight, int desiredWidth) {
        setImage(url, new ColorDrawable(getContext().getResources().getColor(R.color.transparent)), desiredHeight, desiredWidth);
    }
    
    public void setImage(String url, int desiredHeight, int desiredWidth, Boolean refreshCached) {
        setImage(url, new ColorDrawable(getContext().getResources().getColor(R.color.transparent)), desiredHeight, desiredWidth, refreshCached);
    }

    public void setImage(String url, int placeholderDrawableId) {
        setImage(url, getContext().getResources().getDrawable(placeholderDrawableId));
    }
    
    public void setImage(String url, int placeholderDrawableId, Boolean refreshCached) {
        setImage(url, getContext().getResources().getDrawable(placeholderDrawableId), refreshCached);
    }


    public void setImage(String url, Drawable placeholderDrawable) {
        setImage(url, placeholderDrawable, -1, -1);
    }

    public void setImage(String url, Drawable placeholderDrawable, Boolean refreshCached) {
        setImage(url, placeholderDrawable, -1, -1, refreshCached);
    }
    

    public void setImage(String url, Drawable placeholderDrawable, int desiredHeight, int desiredWidth, Boolean refreshCached) {
        final ShutterbugManager manager = ShutterbugManager.getSharedImageManager(getContext());
        manager.cancel((ShutterbugManagerListener) this);
        
        if (refreshCached == Boolean.TRUE)
        {
        	String cacheKey = ShutterbugManager.getCacheKey(url);
        	if (cacheKey != null)
        	{
        		ImageCache imageCache = ImageCache.getSharedImageCache(getContext());
        		
        		// Only check the in-memory cache, unlike in ImageCache class
	            Bitmap cachedBitmap = imageCache.getBitmapFromCache(cacheKey);

	            if (cachedBitmap != null) {
	                setImageBitmap(cachedBitmap);
	            }
	        	else
	        	{
	            	setImageDrawable(placeholderDrawable);        		
	        	}
        	}
        	else
        	{
            	setImageDrawable(placeholderDrawable);        		
        	}
        }
        else
        {
        	setImageDrawable(placeholderDrawable);
        }
        
        if (url != null) {
            manager.download(url, (ShutterbugManagerListener) this, desiredHeight, desiredWidth);
        }
    }
    
    public void setImage(String url, Drawable placeholderDrawable, int desiredHeight, int desiredWidth) {
    	setImage(url, placeholderDrawable, desiredHeight, desiredWidth, false);
    }

    public void cancelCurrentImageLoad() {
        ShutterbugManager.getSharedImageManager(getContext()).cancel((ShutterbugManagerListener) this);
    }
    
    @Override
    public void setImageDrawable(Drawable drawable)
    {
    	clearImageBitmap();    	
    	super.setImageDrawable(drawable);
    }
    
    @Override
    public void setImageBitmap(Bitmap bitmap)
    {
    	clearImageBitmap();    	
    	super.setImageBitmap(bitmap);
    }
    
    private void clearImageBitmap()
    {
    	try
    	{
    		((BitmapDrawable)this.getDrawable()).getBitmap().recycle();
    	}
    	catch (Exception err)
    	{
    		
    	}
    }

    @Override
    public void onImageSuccess(ShutterbugManager imageManager, Bitmap bitmap, String url) {
    	
    	
    	setImageBitmap(bitmap);
        requestLayout();
        if (mListener != null) {
            mListener.onImageFetched(bitmap, url);
        }
    }

    @Override
    public void onImageFailure(ShutterbugManager imageManager, String url) {
        if (mListener != null) {
            mListener.onImageFailure(url);
        }
    }

}
