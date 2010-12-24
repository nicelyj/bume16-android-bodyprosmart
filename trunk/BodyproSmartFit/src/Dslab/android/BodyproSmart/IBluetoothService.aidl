package Dslab.android.BodyproSmart;

interface IBluetoothService{
	int startSearch(in int msg);
	int	getindex(in int msg);
	String getString(in int nidx);		
}