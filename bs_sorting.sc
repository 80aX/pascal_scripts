// скрипт на изотовление и сортировку по контейреам кос. Хавает фишстейки,
// качает армслор, проверяет наличие пустых контейнеров и металла в паке. Если
// заказнчивается металл, ищет под ногами новый и забирает его в пак.
// Наполненные контейнеры скидывает в рядом стоящий сундук.
// v0.1 made by 80aX for ZHR


program bs_sorting;
{$Include 'all.inc'}

const
Ingots = $1BF2;       // тип руды
Hammer = $13E3;       // тип молототка
Container1 = $0E79;   // тип пончи для расфасовки
Container2 = $09B0;   // тип пончи для расфасовки
Sunduk = $43974C6F;   // id сундука куда скидывать заполненные пончи

var x: integer;


Procedure ArmsLore;
begin
    if TargetPresent then CancelTarget;
    UseSkill('Arms Lore');
    WaitTargetObject(FindType($0F51,Backpack));
    Wait(200);
end;   


Procedure CheckQuantity;
label flag;

begin
flag:
    FindType(Ingots,Backpack);
    AddToSystemJournal('Ingots left: ' + IntToStr(FindFullQuantity));
    if (FindFullQuantity < 50) then
        begin
        if (FindFullQuantity > 0) then begin MoveItem(FindItem,100,Sunduk,0,0,0); Wait(500); end;
        FindDistance := 2;
        FindType(Ingots,Ground);
        if (FindCount = 0) then
            begin
            AddToSystemJournal('No more ingots around');
            Wait(10000);
            ArmsLore;
            goto flag;
            end;
        MoveItem(FindItem,10000,Backpack,0,0,0);
        AddToSystemJournal('Picked up some ingots');
        Wait(500);
        goto flag;
        end;
end;


Procedure MakeItem;
begin
    CancelMenu;
    WaitMenu('to make', 'Weapons');
    WaitMenu('kind', 'Swords');
    WaitMenu('to make', 'Scythe');
    WaitTargetObject(FindType(Ingots,Backpack));
    UseType(Hammer,$FFFF);
end;


procedure GoSort(ItemType: Word);
var ItemToSort, Sumka: Cardinal;

begin
if (Count(ItemType) > 0) then
    begin
    if (Count(Container1) > 0) then begin Sumka := FindType(Container1,Backpack); end
    else if (Count(Container2) > 0) then begin Sumka := FindType(Container2,Backpack); end
    else begin AddToSystemJournal('No more containers'); SetARStatus(false); Disconnect; end;   
    UseObject(Sumka);
        repeat
        if (Count(ItemType) <= 0) then exit;
        ItemToSort := FindType(ItemType,backpack);
        MoveItem(ItemToSort,1,Sumka,0,0,0);
        Wait(500);
        until (CountEx(ItemType,$FFFF,Sumka) <= 299);
    if (CountEx(ItemType,$FFFF,Sumka) > 298) then
        begin
        AddToSystemJournal('Filled one more container');
        MoveItem(Sumka,1,Sunduk,0,0,0);
        Wait(500);
        end;
    end;
end;


procedure AddItemToContainer(Obj, Cont: Cardinal);
begin
    if (Cont = Backpack) and (GetQuantity(Obj) = 1) then GoSort(GetType(Obj));
end;


Begin
SetARStatus(true);
SetEventProc(evAddItemToContainer,'AddItemToContainer');
    repeat
    Hungry(1,Backpack);
    Wait(1000);
    UOSay('.fixme');
    Wait(1000);
    for x := 0 to 100 do
        begin
        if (x MOD 2 = 0) then ArmsLore;
        CheckSave;
        CheckQuantity;
        MakeItem;
        Wait(5700);
        end;
    until Dead or (not Connected);
End.