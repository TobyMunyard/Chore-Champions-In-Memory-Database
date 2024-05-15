-- Create the table to enter the data into
CREATE TABLE Chore(
    Id int PRIMARY KEY, 
    Username VARCHAR(255) NOT NULL,
    Flat VARCHAR(255) NOT NULL,
    Chore VARCHAR(255) NOT NULL,
    Chorescompleted INTEGER NOT NULL
);

-- Insert a dummy value into the table
insert into Chore(Id,Username,Flat,Chore,Chorescompleted) values (1, 'Toby', 'The dairy', 'chore', 7);