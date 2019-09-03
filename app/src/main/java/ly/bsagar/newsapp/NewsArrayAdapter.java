    package ly.bsagar.newsapp;

    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.TextView;

    import java.util.ArrayList;

    public class NewsArrayAdapter extends ArrayAdapter<News> {
        public NewsArrayAdapter(Context context, int resource, ArrayList<News> objects) {
            super(context, resource, objects);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
        }

        News currentNews = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView sectionTextView = convertView.findViewById(R.id.section);
        TextView datePublishedTextView = convertView.findViewById(R.id.datePublished);
        TextView authorNameTextView = convertView.findViewById(R.id.authorName);

        titleTextView.setText(currentNews.getWebTitle());
        sectionTextView.setText(currentNews.getSectionName());
        if (currentNews.getWebPublicationDate().isEmpty()){
            datePublishedTextView.setVisibility(View.GONE);
        } else {
            datePublishedTextView.setText(currentNews.getWebPublicationDate());
        }

        authorNameTextView.setVisibility(View.GONE);

        return convertView;
    }
}
