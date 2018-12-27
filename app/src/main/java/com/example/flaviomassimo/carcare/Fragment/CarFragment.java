package com.example.flaviomassimo.carcare.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.flaviomassimo.carcare.Activities.AddCarActivity;
import com.example.flaviomassimo.carcare.Activities.InterventionActivity;
import com.example.flaviomassimo.carcare.Activities.SharingValues;
import com.example.flaviomassimo.carcare.DataBase.Car;
import com.example.flaviomassimo.carcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listKeys = new ArrayList<String>();
    ArrayAdapter<String> adapter;


    private ListView list_cars;
    private TextView noCars;
    private TextView carInfo;
    private Iterable<DataSnapshot> cars,infos;
    private boolean existingCars=false;
    private OnFragmentInteractionListener mListener;

    private Button addCar;
    private Button addInterventions;
    private Button updateCarInfo;
    private Car car=new Car();
    private DatabaseReference mRef;
    String UID;
    FirebaseUser user;
    public CarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarFragment newInstance(String param1, String param2) {
        CarFragment fragment = new CarFragment();
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
        final View view= inflater.inflate(R.layout.fragment_car, container, false);
        list_cars = (ListView) view.findViewById(R.id.carsList);

        addCar=(Button) view.findViewById(R.id.addButton);
        addCar.setOnClickListener(this);
        updateCarInfo=(Button) view.findViewById(R.id.update_car_info);
        updateCarInfo.setOnClickListener(this);
        updateCarInfo.setVisibility(View.GONE);
        addInterventions=(Button) view.findViewById(R.id.interventions);
        addInterventions.setOnClickListener(this);
        addInterventions.setVisibility(View.GONE);

        carInfo=(TextView) view.findViewById(R.id.carInfo);
        carInfo.setVisibility(View.GONE);
        noCars =(TextView) view.findViewById(R.id.noCars);
        user= FirebaseAuth.getInstance().getCurrentUser();
        UID=user.getUid().toString();

        mRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://carcare-dce03.firebaseio.com/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(UID).hasChild("Cars"))existingCars=false;
                else{
                    noCars.setVisibility(View.INVISIBLE);
                    existingCars=true;
                    cars=dataSnapshot.child("Users").child(UID).child("Cars").getChildren();

                        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);
                        list_cars.setAdapter(adapter);
                        list_cars.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        list_cars.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        updateCarInfo.setVisibility(View.VISIBLE);
                                        addInterventions.setVisibility(View.VISIBLE);
                                        carInfo.setVisibility(View.VISIBLE);
                                        addCar.setVisibility(View.GONE);
                                        String plate=listItems.get(position).toString();
                                        infos= dataSnapshot.child("Users").child(UID).child("Cars").child(plate).getChildren();
                                        String temp="Plate:   "+plate+"\n";
                                        Car newCar=new Car(plate);
                                        newCar.setMODEL(dataSnapshot.child("Users").child(UID).child("Cars").child(plate).child("Model").getValue().toString());
                                        newCar.setMAKE(dataSnapshot.child("Users").child(UID).child("Cars").child(plate).child("Make").getValue().toString());
                                        newCar.setKM(Double.parseDouble(dataSnapshot.child("Users").child(UID).child("Cars").child(plate).child("Km").getValue().toString()));
                                        newCar.setFUEL_TYPE(dataSnapshot.child("Users").child(UID).child("Cars").child(plate).child("Fuel").getValue().toString());
                                        SharingValues.setCar(newCar);

                                            temp+="Make:   "+newCar.getMAKE()+"\n"+"Model:   "+newCar.getMODEL()+"\n";
                                            temp+="Km:   "+newCar.getKM()+"\n"+"Fuel:   "+newCar.getFUEL_TYPE()+"\n";


                                        carInfo.setText(temp);
                                    }
                                });
                        adapter.clear();
                        listItems.clear();
                        while (cars.iterator().hasNext()) {
                            DataSnapshot singlecar = cars.iterator().next();
                            String plate = singlecar.getKey().toString();
                            listItems.add(plate);
                        }


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        System.out.println("ExistingCArs "+existingCars);
        System.out.println(cars);


        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);

        }
    }

//TODO Vedere l'onBackPressed per i fragment, al fi<ne di evitare l'uscita dall'app
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.addButton){ Intent intent=new Intent(getActivity(), AddCarActivity.class); startActivity(intent); }
        if(i==R.id.update_car_info){
            Intent intent=new Intent(getActivity(), AddCarActivity.class); startActivity(intent);
        }
        if(i==R.id.interventions){Intent intent=new Intent(getActivity(), InterventionActivity.class); startActivity(intent);}
    }






    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int i= view.getId();
        if(i==R.id.show_dropdown_menu){
            switch (position) {
                case 0:
                    car.setFUEL_TYPE("Gasoline");
                    break;
                case 1:
                    car.setFUEL_TYPE("Petrol");
                    break;
                case 2:
                    car.setFUEL_TYPE("GPL");
                    break;
                case 3:
                    car.setFUEL_TYPE("Other");
                    break;
            }


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
