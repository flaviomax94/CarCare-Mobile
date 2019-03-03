package com.example.flaviomassimo.carcare.Fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flaviomassimo.carcare.Activities.AddCarActivity;
import com.example.flaviomassimo.carcare.Activities.PreviousPathsActivity;
import com.example.flaviomassimo.carcare.Activities.SharingValues;
import com.example.flaviomassimo.carcare.DataBase.Car;
import com.example.flaviomassimo.carcare.DataBase.Path;
import com.example.flaviomassimo.carcare.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PathsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PathsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PathsFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<String> listItemsCars = new ArrayList<String>();
    ArrayList<String> listItemsPaths = new ArrayList<String>();
    ArrayAdapter<String> adapterCars,adapterPaths;
    private StorageReference mStorageRef;
    private DatabaseReference mDataBase;
    String UID;
    FirebaseUser user;
    File localFile;
    private ListView list_cars;
    private ListView list_paths;

    private TextView noCars,noPaths;
    private Iterable<DataSnapshot> cars,paths;

    private boolean existingCars=false;
    private Button seePath,addCars;
    private Car car=new Car();
    private DatabaseReference mRef;
    private String plate;
    private OnFragmentInteractionListener mListener;

    public PathsFragment() {
        // Required empty public constructor
    }


    public static PathsFragment newInstance(String param1, String param2) {
        PathsFragment fragment = new PathsFragment();
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
        final View view= inflater.inflate(R.layout.fragment_paths, container, false);
        list_cars = (ListView) view.findViewById(R.id.carsList);
        list_paths = (ListView) view.findViewById(R.id.pathsList);

        seePath=(Button) view.findViewById(R.id.seePath);
        seePath.setOnClickListener(this);
        addCars=(Button) view.findViewById(R.id.addButton);
        seePath.setOnClickListener(this);
        seePath.setVisibility(View.GONE);
        addCars.setVisibility(View.VISIBLE);
        noCars =(TextView) view.findViewById(R.id.noCars);
        noCars.setVisibility(View.VISIBLE);
        noPaths =(TextView) view.findViewById(R.id.noPaths);
        noPaths.setVisibility(View.GONE);
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(UID).hasChild("Cars"))existingCars=false;
                else{
                    noCars.setVisibility(View.INVISIBLE);
                    addCars.setVisibility(View.GONE);
                    existingCars=true;
                    cars=dataSnapshot.child("Users").child(UID).child("Cars").getChildren();

                    adapterCars = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItemsCars);
                    list_cars.setAdapter(adapterCars);
                    list_cars.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                    adapterPaths = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItemsPaths);
                    list_paths.setAdapter(adapterPaths);
                    list_paths.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                    list_cars.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            adapterPaths.clear();
                            listItemsPaths.clear();
                            list_paths.setVisibility(View.VISIBLE);
                            noPaths.setVisibility(View.GONE);
                            plate=listItemsCars.get(position).toString();

                            if(plate!=null){
                                if(!dataSnapshot.child("Users").child(UID).child("Cars").child(plate).child("Paths").hasChildren()){
                                    list_paths.setVisibility(View.GONE);
                                    noPaths.setVisibility(View.VISIBLE);
                                }
                            }
                            paths=dataSnapshot.child("Users").child(UID).child("Cars").child(plate).child("Paths").getChildren();

                            while (paths.iterator().hasNext()) {
                                System.out.println("--------------ELEMENT------------------");
                                DataSnapshot singlePath = paths.iterator().next();
                                String pathKey = singlePath.getKey().toString();
                                listItemsPaths.add(pathKey);
                            }

                        }
                    });

                    list_paths.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getContext(),"Please wait, the download is starting",Toast.LENGTH_SHORT).show();
                            String pathName=listItemsPaths.get(position).toString();
                            System.out.println(pathName);
                            String pathURL= dataSnapshot.child("Users").child(UID).child("Cars").child(plate).child("Paths").child(pathName).getValue().toString();
                            System.out.println(pathURL);
                            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(pathURL);

                                localFile =new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"filePath.txt");
                            try {
                                localFile.createNewFile();
                            } catch (IOException e) {
                                System.out.println("ERROR IN CREATION OF TEMP FILE----------");
                            }
                            mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getContext(),"Path ready to be seen!",Toast.LENGTH_SHORT).show();
                                    SharingValues.setPath(localFile);
                                    System.out.println("FILE DOWNLOADED-------------");
                                    System.out.println(SharingValues.getPath().toString());
                                    seePath.setVisibility(View.VISIBLE);
                                    seePath.setClickable(true);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    System.out.println("ERROR ON DOWNLOAD FILE FROM STORAGE------");
                                }
                            });



                        }
                    });


                    adapterCars.clear();
                    adapterPaths.clear();
                    while (cars.iterator().hasNext()) {
                        DataSnapshot singlecar = cars.iterator().next();
                        String plate = singlecar.getKey().toString();
                        listItemsCars.add(plate);
                    }


        }
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

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
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.seePath){ Intent intent=new Intent(getActivity(), PreviousPathsActivity.class); startActivity(intent); }
        if(i==R.id.addButton){ Intent intent=new Intent(getActivity(), AddCarActivity.class); startActivity(intent); }
    }
}
