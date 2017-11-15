// скрипт на добычу перьев. Чар стоит на респе, при появлении птиц в зоне
// поиска подходит, убивает и режет перья, всякий хлам выкидывает на пол.
// Хавает время от времени фишстейки, пока ждёт уходит в хайд.
// v0.1 made by 80aX for ZHR


Program birds_killer; 
{$Include 'all.inc'}

Const
Weapon = $13FE;     // тип оружия которым расчленять
Corpse = $2006;     // тип трупа
Feather = $1BD1;    // тип перьев

Var
Bird: array [1..2] of Cardinal;
CorpseID, Victim: Cardinal;
m, x: Integer;


procedure Inittypes;
begin
    Bird[1] := $0006;
    Bird[2] := $00D0;
end;


procedure Raschlenenie;
begin
FindDistance := 2;
FindType(Corpse,Ground);
if (FindCount > 0) then
    begin
    if TargetPresent then CancelTarget;
    CorpseID := FindType(Corpse,Ground);
    UseObject(ObjAtLayerEx(RhandLayer,Self));
    WaitForTarget(1000);
    if TargetPresent then TargetToObject(CorpseID);
    Wait(1000);
    AddToSystemJournal(IntToStr(GetQuantity(FindType(Feather,backpack))) + ' Перьев')
    Ignore(CorpseID);
{
    // Выкидываем потроха
    FindType($09B9,backpack);
    if (FindCount > 0) then Drop(FindItem,0,0,0,0);
    Wait(100);
    FindType($1607,backpack);
    if (FindCount > 0) then Drop(FindItem,0,0,0,0);
    Wait(100);
    FindType($1E8A,backpack);
    if (FindCount > 0) then Drop(FindItem,0,0,0,0);
    Wait(100);
}
    end;
end;


procedure Ynichtozhenie;
begin
While GetHP(Victim) > 0 Do
    begin
    NewMoveXY(GetX(Victim),GetY(Victim),True,1,True);
    FindDistance := 1;
    Attack(Victim);
    Wait(1000);
    end;
    SetWarMode(False);
end;


procedure AttackChecking;
begin
if (WarMode = false) then SetWarMode(True);
if (ObjAtLayer(RhandLayer) = 0) then
    begin
    Equip(RhandLayer,FindType(Weapon,backpack));
    Wait(1000);
    end;
end;


procedure BirdSearching;
begin
for m := 1 to 2 do
    begin
    Victim := 0;
        repeat
        FindDistance := 20;
        Victim := FindType(Bird[m],Ground);
        if Victim = 0 then break;
        AddToSystemJournal('Найдено '+inttostr(FindCount)+' '+GetName(Victim));
        AttackChecking;
        Ynichtozhenie;
        Raschlenenie;
        CheckSave;
        until Victim = 0;
    end;
end;

procedure GoHide;
begin
    UseSkill('Hiding');
    Wait(10000);
end;


BEGIN
Inittypes;
SetARStatus(true);
AddChatUserIgnore('Mansur');
    repeat
    Hungry(1, Backpack);
    Wait(1000);
    for x := 0 to 1000 do
        begin
        BirdSearching;
        if (not hidden) then GoHide;
        Wait(1000);
        end;
    until Dead or (not Connected);
END.