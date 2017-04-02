package com.codeground.wanderlustbulgaria.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.Activities.ChatActivity;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class WanderersFragment extends Fragment{

    /** The Chat list. */
    private ArrayList<ParseUser> uList;

    /** The user. */
    public static ParseUser user;

    /** Flag to hold if the activity is running or not */
    private boolean isRunning;

    /** The handler */
    private static Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wanderers, container, false);

        user = ParseUser.getCurrentUser();

        handler = new Handler();

        return v;
    }

    public static WanderersFragment newInstance() {

        WanderersFragment f = new WanderersFragment();

        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserList();
        isRunning = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
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
            DialogWindowManager.show(getActivity());
        }

        ParseUser.getQuery().whereNotEqualTo("username", user.getUsername()).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                DialogWindowManager.dismiss();

                if(users != null){
                    if(users.size() == 0){
                        Toast.makeText(getActivity(), "No users found", Toast.LENGTH_SHORT);
                    }

                    uList = new ArrayList<ParseUser>(users);
                    ListView list = (ListView) getView().findViewById(R.id.list);
                    list.setAdapter(new WanderersFragment.UserAdapter());
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
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
                v = getActivity().getLayoutInflater().inflate(R.layout.chat_item, null);

            ParseUser c = getItem(pos);
            TextView lbl = (TextView) v;
            lbl.setText(c.getString("first_name") + " " + c.getString("last_name"));
            lbl.setCompoundDrawablesWithIntrinsicBounds(
                    c.getBoolean("online") ? R.drawable.ic_online
                            : R.drawable.ic_offline, 0, R.drawable.arrow, 0);

            return v;
        }

    }
}