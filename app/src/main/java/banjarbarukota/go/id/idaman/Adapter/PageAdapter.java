package banjarbarukota.go.id.idaman.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import banjarbarukota.go.id.idaman.Fragments.Langkah1Fragment;
import banjarbarukota.go.id.idaman.Fragments.Langkah2Fragment;
import banjarbarukota.go.id.idaman.Fragments.Langkah3Fragment;

public class PageAdapter extends FragmentPagerAdapter {
 
    public PageAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new Langkah1Fragment();
        case 1:
            // Games fragment activity
            return new Langkah2Fragment();
	    case 2:
	        // Games fragment activity
	        return new Langkah3Fragment();
	    }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}
