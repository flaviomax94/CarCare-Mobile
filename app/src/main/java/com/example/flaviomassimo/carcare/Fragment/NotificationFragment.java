package com.example.flaviomassimo.carcare.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.flaviomassimo.carcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView list_notifications;
    private TextView noNotification;
    private DatabaseReference mDataBase;
    private Iterable<DataSnapshot> notifications;
    ArrayList<String> listItemsNotifications = new ArrayList<String>();
    ArrayAdapter<String> adapterNotifications;
    String UID;
    FirebaseUser user;
    private OnFragmentInteractionListener mListener;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_notification, container, false);
        list_notifications = (ListView) view.findViewById(R.id.notificationsList);
        noNotification =(TextView) view.findViewById(R.id.noNotification);
        noNotification.setVisibility(View.VISIBLE);
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mDataBase= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(UID).hasChild("Notifications")){
                    noNotification.setVisibility(View.GONE);
                    notifications=dataSnapshot.child("Users").child(UID).child("Notifications").getChildren();
                    adapterNotifications = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItemsNotifications);
                    list_notifications.setAdapter(adapterNotifications);
                    list_notifications.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    list_notifications.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    });

                    adapterNotifications.clear();
                    while (notifications.iterator().hasNext()) {
                        DataSnapshot singlenotification = notifications.iterator().next();
                        String date = singlenotification.getKey().toString();
                        String value=singlenotification.getValue().toString();
                        listItemsNotifications.add(date+"\n\t\t"+value);
                    }

                }
                else{
                    list_notifications.setVisibility(View.GONE);
                    noNotification.setVisibility(View.VISIBLE);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
