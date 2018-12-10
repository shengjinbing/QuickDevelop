package com.modesty.quickdevelop.network.rx;

import android.content.Context;

import com.modesty.quickdevelop.network.ui.ProgressDialog;

import org.reactivestreams.Subscriber;

import io.reactivex.subscribers.ResourceSubscriber;


/**
 * <p>
 * Extension of {@link Subscriber}. For better extension and customization, client can extend this
 * class to override the default behaviours such as loading appearance on each lifecycle method
 * of network callback.<p/>
 *
 * @author wangzhiyuan
 * @since 2017/5/20
 */

public abstract class SubscriberEx<T> extends ResourceSubscriber<T> {
    private static final String DEF_LOADING_TEXT = "正在加载中...";
    private Context mAttachedContext;
    private String mLoadingMessage;
    private boolean mCancellable;
    private boolean mCancellableOnTouchOutSide;
    private ProgressDialog mProgressDialog;

    /**
     * Default constructor, in this case, no loading dialog will appear when network is ongoing.
     */
    public SubscriberEx() {
    }

    /**
     * Constructor with the attached context.
     * Default dialog and ongoing message will be displayed in this case.
     *
     * @param attachedContext the attached context
     */
    public SubscriberEx(Context attachedContext) {
        this(attachedContext, DEF_LOADING_TEXT, true);
    }

    /**
     * Constructor with context and loading message
     *
     * @param attachedContext context
     * @param loadingMessage loading message
     */
    public SubscriberEx(Context attachedContext, String loadingMessage) {
        this(attachedContext, loadingMessage, true);
    }

    /**
     * Constructor with context, loading message and indicator which indicates that loading dialog can
     * be cancelled.
     *
     * @param attachedContext context
     * @param loadingMessage loading message
     * @param cancellable    {@code true} means dialog can be cancelled
     */
    public SubscriberEx(Context attachedContext, String loadingMessage, boolean cancellable) {
        this(attachedContext,loadingMessage,cancellable,false);
    }
    /**
     * Constructor with context, loading message and indicator which indicates that loading dialog can
     * be cancelled.
     *
     * @param attachedContext context
     * @param loadingMessage loading message
     * @param cancellable    {@code true} means dialog can be cancelled
     * @param cancellableOnTouchOutSide {@code true} means dialog can be cancelled on touching outside of it.
     */
    public SubscriberEx(Context attachedContext, String loadingMessage, boolean cancellable, boolean cancellableOnTouchOutSide) {
        this.mAttachedContext = attachedContext;
        this.mLoadingMessage = loadingMessage;
        this.mCancellable = cancellable;
        this.mCancellableOnTouchOutSide = cancellableOnTouchOutSide;
        this.mProgressDialog = new ProgressDialog();
    }

    /**
     * This method must be called if you want to use the default loading dialog in case of override.
     * Otherwise you can ignore it.
     */
    @Override
    public void onStart() {
        super.onStart();
        if(mProgressDialog != null){
            mProgressDialog.showLoadingDialog(mAttachedContext, mLoadingMessage, mCancellable, mCancellableOnTouchOutSide);
        }
    }

    /**
     * This method must be called if you want to use the default loading dialog in case of override.
     * Otherwise you can ignore it.
     */
    public void onFinish(){
        if(mProgressDialog != null){
            mProgressDialog.removeLoadingDialog();
            mProgressDialog = null;
        }
    }

    /**
     * This method must be called if you want to use the default loading dialog in case of override.
     * Otherwise you can ignore it.
     */
    @Override
    public void onComplete() {
        onFinish();
    }

    /**
     * This method must be called if you want to use the default loading dialog in case of override.
     * Otherwise you can ignore it.
     */
    @Override
    public void onError(Throwable e) {
        onFinish();
    }

    /**
     * This method must be override if you care about the result of request.
     *
     * @param o The result of network request
     */
    @Override
    public void onNext(T o) {

    }

}
