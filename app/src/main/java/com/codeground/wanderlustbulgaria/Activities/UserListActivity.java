package com.codeground.wanderlustbulgaria.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class UserList is the Activity class. It shows a list of all users of
 * this app. It also shows the Offline/Online status of users.
 */
public class UserListActivity extends CustomActivity
{
	/** The Chat list. */
	private ArrayList<ParseUser> uList;

	/** The user. */
	public static ParseUser user;

    /** Flag to hold if the activity is running or not */
    private boolean isRunning;

    /** The handler */
    private static Handler handler;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_wanderers);
        user = ParseUser.getCurrentUser();



        ActionBar actionBar = getActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(R.string.messenger_btn);
        }
        handler = new Handler();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy()
	{
		super.onDestroy();

	}

	/* (non-Javado
	c)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
        isRunning = true;
		loadUserList();

	}

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

	/**
	 * Load list of users.
	 */
	private void loadUserList()
	{
        if(uList==null){
            //first loading... the screen is empty now
            DialogWindowManager.show(this);
        }

		ParseUser.getQuery().whereNotEqualTo("username", user.getUsername()).findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				DialogWindowManager.dismiss();

				if(users != null){
					if(users.size() == 0){
						Toast.makeText(getApplicationContext(), "No users found", Toast.LENGTH_SHORT);
					}

					uList = new ArrayList<ParseUser>(users);
					ListView list = (ListView) findViewById(R.id.list);
					list.setAdapter(new UserAdapter());
					list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
							intent.putExtra("username", uList.get(position).getUsername());
							intent.putExtra("full_name", uList.get(position).getString("first_name") + " " + uList.get(position).getString("last_name"));
							startActivity(intent);
						}
					});
				}else{
                    //TODO notify error with custom dialog
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isRunning){
                            loadUserList();
                        }
                    }
                },5000);
			}
		});
	}

	/**
	 * The Class UserAdapter is the adapter class for User ListView. This
	 * adapter shows the user name and it's only online status for each item.
	 */
	private class UserAdapter extends BaseAdapter
	{

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount()
		{
			return uList.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public ParseUser getItem(int arg0)
		{
			return uList.get(arg0);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int pos, View v, ViewGroup arg2)
		{
			if (v == null)
				v = getLayoutInflater().inflate(R.layout.chat_item, null);

			ParseUser c = getItem(pos);
			TextView lbl = (TextView) v;
			lbl.setText(c.getString("first_name") + " " + c.getString("last_name"));
			lbl.setCompoundDrawablesWithIntrinsicBounds(
					c.getBoolean("online") ? R.drawable.ic_online
							: R.drawable.ic_offline, 0, R.drawable.arrow, 0);

			return v;
		}

	}

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
