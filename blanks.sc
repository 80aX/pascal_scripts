// скрипт на изготовление блак скроллов и бланк мапов. В паке должно быть дерево
// и инструмент. Хавает фишстейки, проверяет количество дерева, если дерево
// заканчивается качает армслор.
// v0.1 made by 80aX for ZHR


Program Blanks;
{$Include 'all.inc'}
 
const
LogsType = $1BDD;     // тип логов            
TolsType = $1EBC;     // тинкер тоолс
Sumka = $42F3F2D0;    // сумка куда складывать

var
x: integer;


Procedure BlankScrolls;
begin
    CancelMenu;
    Waitmenu('to make', 'Paper items');
    Waitmenu('to make', 'Blank Scroll');
    UseObject(FindType(TolsType,Backpack));
    WaitTargetObject(FindType(LogsType,Backpack));
    Wait(6600);
end;


Procedure BlankMapsMove;
begin
while (Count($14EB) > 0) do
    begin
    FindType($14EB,Backpack);
    if (FindQuantity > 0) then MoveItem(FindItem,1,Sumka,0,0,0);
    Wait(500);
    end;
end;


Procedure BlankMaps;
begin
    CancelMenu;
    Waitmenu('to make', 'Paper items');
    Waitmenu('to make', 'Blank Map');
    UseObject(FindType(TolsType,Backpack));
    WaitTargetObject(FindType(LogsType,Backpack));
    Wait(6600);
    BlankMapsMove;
end;


Procedure CheckQuantity;
begin
FindType(LogsType,Backpack);
AddToSystemJournal('Logs left: ' + IntToStr(FindFullQuantity));
if (FindFullQuantity < 50) then
    begin
        repeat
        if TargetPresent then CancelTarget;
        WaitTargetObject(FindType($0F51,Backpack));
        UseSkill('Arms Lore');
        Wait(10000);
        until (GetQuantity(FindType(LogsType,Backpack)) > 50);
    end;
end;


BEGIN
SetARStatus(true);
    repeat
    Hungry(1,Backpack);
    Wait(1000);
    for x := 0 to 100 do
        begin
        CheckSave;
        CheckQuantity;
        BlankScrolls;   // procedure BlankScrolls BlankMaps
        end;
    until Dead or (not Connected);
END.