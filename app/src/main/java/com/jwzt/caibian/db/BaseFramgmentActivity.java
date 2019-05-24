package com.jwzt.caibian.db;


 import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;

 public abstract class BaseFramgmentActivity<H extends DatabaseHelper>
   extends FragmentActivity
 {
   private volatile H helper;
   private volatile boolean created = false;
   private volatile boolean destroyed = false;
  private static Logger logger = LoggerFactory.getLogger(BaseFramgmentActivity.class);

   public H getHelper()
   {
     if (helper == null) {
       if (!created)
        throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
      if (destroyed) {
        throw new IllegalStateException("A call to onDestroy has already been made and the helper cannot be used after that point");
       }
      
      throw new IllegalStateException("Helper is null for some unknown reason");
     }
     
    return helper;
   }

   public ConnectionSource getConnectionSource()
   {
     return getHelper().getConnectionSource();
   }
   
   protected void onCreate(Bundle savedInstanceState)
   {
     if (helper == null) {
       helper = getHelperInternal(this);
       created = true;
     }
     super.onCreate(savedInstanceState);
   }
   
  protected void onDestroy()
   {
     super.onDestroy();
     releaseHelper(helper);
     destroyed = true;
   }

  protected H getHelperInternal(Context context)
   {
     H newHelper = (H) OpenHelperManager.getHelper(context);
     logger.trace("{}: got new helper {} from OpenHelperManager", this, newHelper);
     return newHelper;
   }

   protected void releaseHelper(H helper)
   {
     OpenHelperManager.releaseHelper();
     logger.trace("{}: helper {} was released, set to null", this, helper);
    this.helper = null;
 }
   
  public String toString()
   {
    return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
  }
 }
