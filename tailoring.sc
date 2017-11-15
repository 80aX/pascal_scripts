// скрипт на изготовление и выкидывание в треш ковров. Хавает фишстейки,
// проверяет количество ткани в паке, если ткань закончилась добирает ещё с пола.
// v0.1 made by 80aX for ZHR


program ssanie_kovri;
{$Include 'all.inc'}

const
Skit = $0F9D;
Cloth = $1765;
Trashbin = $439722D6;

var x: Integer;


procedure ArmsLore;
begin
    repeat
    if TargetPresent then CancelTarget;
    WaitTargetObject(FindType($0F51,backpack));
    UseSkill('Arms Lore');
    Wait(10000);
    until (FindType(Cloth,Ground) > 0);
end;


procedure ToTrash(ItemType: Word);
begin
while (FindType(ItemType,Backpack) > 0) do
    begin
    MoveItem(FindType(ItemType,Backpack),1,Trashbin,0,0,0);
    Wait(1000);
    end;
end;


procedure MakeItem;
begin
    CancelMenu;
    WaitMenu('What','Carpet');
    WaitMenu('make','Persian Rag');
    UseObject(FindType(Skit,Backpack));
    FindTypeEX(Cloth,$FFFF,Backpack,false);
    WaitTargetObject(FindItem);
end;


procedure GrabMoreCloth;
begin
    FindDistance := 2;
    if FindType(Cloth,Ground) > 0 then
        begin 
        MoveItem(FindItem,10000,Backpack,0,0,0);
        AddToSystemJournal('Picked up some cloth');
        Wait(1000);
        end
    else
        begin
        AddToSystemJournal('No more cloth around');
        ArmsLore;
        end;
end;


procedure CheckQuantity;
begin
    FindType(Cloth,Backpack);
    AddToSystemJournal('Cloth left: ' + IntToStr(FindFullQuantity));
    if (FindFullQuantity < 100) then GrabMoreCloth;
end;

procedure AddItemToContainer(Obj, Cont: Cardinal);
begin
    if (Cont = Backpack) and (GetQuantity(Obj) = 1) then ToTrash(GetType(Obj));
end;


Begin
SetARStatus(true);
SetEventProc(evAddItemToContainer,'AddItemToContainer');
while (not Dead) and (Connected) do
    begin
    Hungry(1,Backpack);
    Wait(1000);
    for x := 0 to 1000 do
        begin
        CheckSave;
        CheckQuantity;
        MakeItem;
        Wait(1000);
        end;
    end;
End.
