## Recent-Stacks

This is a native android app built with Kotlin, which fetches a list of the recent stack overflow questions and displays them in a list.
The user can search the list of questions using the search bar provided and can also filter based on the tags of the questions.
There is offline support to see the questions that were fetched last time the device was online.

## Application Architecture

The app is built using the MVVM architecture which stands for Model-View-ViewModel. The UI components is only responsible for managing the
views / activity. The main business logic lies with the ViewModel - this decides what should happen based on the user actions in the ui.
When the ViewModel needs data, it gets it through the Repository. The repository contains the logic for returning the data - either from local db, network call or any other source.

The view does not get the response back from view model directly, but has to subscribe to observables exposed by the ViewModel to look for changes.

- This application has 3 models - Item, Owner and Question.
  - **Item**  represents a single stackoverflow question, containing the question link, post date etc.
  - **Owner** contains the owner details - name, profile image, etc.
  - **Question** represents the response object received form the api containing the list of **Item**'s

- To get the results from the given web api, **Retrofit** library is used.
- To cache the results obtained from the api in local db for offline support, **Room** database library is used.
- The repository class first checks if the device has an active internet connection or not. If there is an active internet connection, then it fetches the results from the stackoverflow api and then stores it in the local db with Room after deleting the stale entries. If the device is offline then the last fetched questions retrieved from local db are displayed.
- After getting the list of questions, advertisement items are inserted in the list at intervals of 4 list items (5 just for the beginning set). Based on the items, there are view types for the recycler view to show - Ad and Content. A diff util is implemented for the recycler view to efficiently handle changes to the list items. Glide library is used to show the profile pictures of users.
- Debounced search is implemented by enclosing the search functionality in a coroutine with a delay of 500 ms for every search job. When there is no search results a message is shown in a card stating that there are no matching search results.
- A filter is also implemented with which the user can view only certain items having the tag equal to the chosen filter. The filter options are obtained by extracting the tags available in the questions list and taking the unique tags of this list. Of this list of tags only the 10 topmost tags are made available as filters (top 10 because otherwise the filters list was becoming absurdly large).
- In case of a non empty search query which yields a non-empty questions list, the average view count and answer count of the list of questions is shown. These average stats are also shown if the user applies any filter. The calculation of these average stats is done in a background thread with the help of coroutines.
- The search query can be cleared by click the cross button in the search bar. The filter can be cleared by choosing the **All** option from the filter radio group items.
- The question post date is shown in **dd-mm-yyyy** format.
- On clicking a question item, the question link is opened in the mobile browser if the device is online. If the user is offline a toast message is shown to the user indicating the same.

## Demo

- First Screen
  ![first screent][1]
- On searching list
  ![search screenshot][2]
- Empty state for no search result
  ![empty state screenshot][3]
- Filters for the list
  ![filters list image][4]
- On applying **tkinter** filter
  ![filter applied state image][5]
- Offline functionality (Note the airplane mode icon on the very top of the device)
  ![offline functionality image][6]


[1]: https://he-s3.s3.amazonaws.com/media/uploads/0ea1381.png
[2]: https://he-s3.s3.amazonaws.com/media/uploads/560e3ad.png
[3]: https://he-s3.s3.amazonaws.com/media/uploads/8ac68ea.png
[4]: https://he-s3.s3.amazonaws.com/media/uploads/aeabbfd.png
[5]: https://he-s3.s3.amazonaws.com/media/uploads/e40419b.png
[6]: https://he-s3.s3.amazonaws.com/media/uploads/1d7514d.png