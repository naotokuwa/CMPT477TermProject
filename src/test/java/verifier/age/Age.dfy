// this method returns the age, given a birthyear, with the assumption that the current year is 2024

// ========================================= valid methods =========================================
method AgeValidWithPre(birthyear: int) returns (age: int)
    requires birthyear <= 2024
    ensures age == 2024 - birthyear
{
    var year := 2024;
    age := 2024 + (-1 * birthyear);
}

method AgeValidWithoutPre(birthyear: int) returns (age: int)
    ensures age == 2024 - birthyear
{
    var year := 2024;
    age := 2024 + (-1 * birthyear);
}


// ======================================== invalid methods ========================================
method AgeInvalidWithPre(birthyear: int) returns (age: int)
    requires birthyear <= 2024
    ensures age == 2024 + birthyear
{
    var year := 2024;
    age := 2024 + (-1 * birthyear);
}

method AgeInvalidWithoutPre(birthyear: int) returns (age: int)
    ensures age == 2024 + birthyear
{
    var year := 2024;
    age := 2024 + (-1 * birthyear);
}


// ======================================== wrong methods ========================================
method AgeInvalidValidSpec(birthyear: int) returns (age: int)
    ensures age == 2024 - birthyear
{
    var year := 2024;
    age := 2024 + birthyear;
}
