## library-spring

library management system 
# ! ITS CONFIGURED TO WORK ON PORT '8001' !

# Admin :
	-/admin-home.html -home page dla admina
	-/manageaccounts– zwraca wszystkich użytkowników
	-/maganeaccount – zwraca informacje o konkretnym użytkowniku (jako parametr trzeba podać id). Możliwość edycji konkretnych pól
    Np: http://localhost:8080/admin/manageaccount?userId=1
	-/saveaccountsettings – skrypt do zapisywania w bazie danych zmian wprowadzonych na koncie użytkownika. Jako return uruchamia „/accountsettingssaved”.
	-/accountsettingssaved – zwraca string „Konto zaktualizowane”.
# Employee: 
	-/employee-home.html – home page dla pracownika
	-/users/showusers – zwraca wszystkich użytkowników
	-/books/showbooks – zwraca wszystkie książki
	-/users/showuserinfo – zwraca informacje o konkretnym użytkowniku
    Np: http://localhost:8080/employee/users/showuserinfo?userId=1
	-/books/newbook – póki co zwraca string, ale trzeba przekminić i zwrócić jakiś formularz żeby dodawało nową książkę.
	-/books/save – zapisuje w bazie danych nową książkę. Jako return uruchamia  „/books/booksaved”
	-/books/booksaved – zwraca string „Dodano książkę”,
	-/books/deletebook  - usuwa książkę z bazy. Jako parametr trzeba podać ID książki [ja bym to na przycisk zrobił].. Jako return uruchamia „/books/bookdeleted”
	-/books/bookdeleted – zwraca string „Usunieto ksiazke”.
	-/books/changebookinfo – wyswietla informacje o danej książce. Jako parametr trzeba podac id. Mozliwosc edycji danych książki.
    Np: http://localhost:8080/employee/books/changebookinfo?changeBookId=1
	-/books/savebookchange – zapisuje zmiany wprowadzone w książce. Uruchamia „/books/bookinfochanged”
    -/books/bookinfochanged – zwraca string „Zapisano zmiany”
    -/returnedbooks – zwraca książki zwrócone do biblioteki, ale chuj wie po co ta funkcje wgl, chyba ją wyjebie finalnie.
    -/reservations – zwraca listę książek które są zarezerwowane
    -/setreadyforpickup – zmienia ststus rezerwacji na „gotowe do odbioru”. Zwraca string „Ksiazka gotowa do odbioru”.
# User:
	-/user/user-home.html – home page użytkownika.
	-/yourbooks – zwraca książki które mam wypożyczone.
	-/browsebooks – przeglądanie wszystkich książek.
	-/yourbooks/extend – przedluzenie wypożyczenia. Jako parametr trzeba id książki i ilość tygodni do przedłużenia [ja bym to na przycisk zrobił]. Zwraca „/yourbooks/bookextended”.
	-/yourbooks/bookextended – zwraca string „Wypozyczenie przedluzone”.
	-/yourreservations – moje rezerwacje





