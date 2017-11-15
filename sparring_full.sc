// скрипт на спарринг. Хавает фишстейки, поверяет наличие брони, каждый элемент
// брони отдельно если он разрушился. Проверяет хп, лечиться, если хп упал до
// критического уровня, отходит на один тайл чтобы отхилиться. Проверяет наличие
// бинтов, армора и оружия. Берёт в руки щит и оружие. Хавает магическую рыбу 
// на хп и декс. 
// v0.1 made by 80aX for ZHR


program sparring_full;
{$Include 'all.inc'}

const
sunduk = $4159CB40; // id сундука с бинтами, армами...
enemy = $00035EC0;  // id противника
bandages = $0E21;   // Тип бинтов
mfish = $0DD6;      // Тип магической рабы на декс
weapon = $1401;     // Тип используемого оружия
shield = $1B7A;     // Тип используемых щитов

EquipFullArm = 1;   // Если 1 надеваем полный армор
EquipWeap = 1;      // Если 1 берём в руки пушку
EquipShield = 1;    // Если 1 берём в руки щит
RaiseDex = 0;       // Если 1 хаваем магическую рыбу
AttackEnemy = 0;    // Если 1 атакуем противника

safex = 2142;       // Координаты отхода
safey = 283;
attackx = 2142;     // Координаты для атаки
attacky = 282;

type ArmLayers = Record
typlayer: Byte;     // Слой
typarm: Cardinal;   // Тип вещи
end;

var
x : Integer;
Ctime: TDateTime;
MyArm : array [0..6] of ArmLayers;


procedure InitArm;
begin
    // Армы и оружие по слоям
    MyArm[0].typlayer := LhandLayer;
    MyArm[0].typarm := $1B7A;
    MyArm[1].typlayer := HatLayer;
    MyArm[1].typarm := $1412;
    MyArm[2].typlayer := NeckLayer;
    MyArm[2].typarm := $1413;
    MyArm[3].typlayer := GlovesLayer;
    MyArm[3].typarm := $1414;
    MyArm[4].typlayer := TorsoLayer;
    MyArm[4].typarm := $1415;
    MyArm[5].typlayer := ArmsLayer;
    MyArm[5].typarm := $1410;
    MyArm[6].typlayer := LegsLayer;
    MyArm[6].typarm := $1411;
end;


procedure CheckQuantites;
begin
    UseObject(sunduk);
    FindType(bandages,sunduk);
    AddToSystemJournal(IntToStr(FindFullQuantity) + ' - bandages');
    if EquipWeap = 1 then
        begin
        FindTypeEx(weapon,$FFFF,sunduk,False);
        AddToSystemJournal(IntToStr(FindCount) + ' - weapons');
        end;
    if EquipShield = 1 then
        begin
        FindTypeEx(shield,$FFFF,sunduk,False);
        AddToSystemJournal(IntToStr(FindCount) + ' - shields');
        end;
    if RaiseDex = 1 then
        begin
        FindTypeEx(mfish,$FFFF,sunduk,False);
        AddToSystemJournal(IntToStr(FindFullQuantity) + ' - magic fishs');
        end;
    FindTypeEx($097B,$FFFF,sunduk,False);
    AddToSystemJournal(IntToStr(FindFullQuantity) + ' - fish steaks');
end;


procedure CheckArm;

VAR i: Integer;
tmpid: Cardinal;

begin
for i := 0 to 6 do
    begin
    if (ObjAtLayer(MyArm[i].typlayer) = 0) then
        begin
        if (Findtype(MyArm[i].typarm,sunduk) = 0) then
            begin
            AddToSystemJournal('Nothing found for: ' + inttostr(MyArm[i].typlayer) + ' layer');
            AddToSystemJournal('Need more $' + inttohex(MyArm[i].typarm,4));
            exit;
            end
        else if (Findtype(MyArm[i].typarm,sunduk) > 0) then
            begin
            AddToSystemJournal('Quantity $' + inttohex(MyArm[i].typarm,4) + ' is: ' + IntToStr(findcount));
            tmpid := finditem;
            Grab(tmpid,1);
            Wait(500);
            end;
        while (ObjAtLayer(MyArm[i].typlayer) <> tmpid) do
            begin
            UnEquip(MyArm[i].typlayer);
            Wait(500);
            Equip(MyArm[i].typlayer,tmpid);
            Wait(500);
            CheckSave;
            end;
        end;
    end;
end;


procedure CheckWeap;
begin
if (GetQuantity(FindType(weapon,sunduk)) > 0) and (GetType(ObjAtLayer(RhandLayer)) <> GetType(FindItem)) then 
    begin
    MoveItem(FindType(weapon,sunduk),1,backpack,0,0,0);
    UnEquip(RHandLayer);
    Wait(500);
    Equip(RhandLayer,FindItem);
    Wait(500);
    end;
end;


procedure CheckShield;
begin
if (GetQuantity(FindType(shield,sunduk)) > 0) and (GetType(ObjAtLayer(LhandLayer)) <> GetType(FindItem)) then 
    begin
    MoveItem(FindType(shield,sunduk),1,backpack,0,0,0);
    UnEquip(LHandLayer);
    Wait(500);
    Equip(LhandLayer,FindItem);
    Wait(500);
    end;
end;


procedure CheckHP;
begin
if (HP < MaxHP / 1.5) and (GetQuantity(FindType(bandages,sunduk)) > 0) then
    begin
    Ctime := Now;
    UseObject(findtype(bandages, sunduk));
    WaitTargetObject(self);
        repeat
        Wait(500);
        if (HP < MaxHP / 3) then exit;
        until (InJournalBetweenTimes('must wait|barely help.|damage healed.', Ctime, Now)<>-1);
    end;
end;


procedure CriticalHP;
begin
SetWarMode(false);
NewMoveXY(safex,safey,true,0,true);
if (GetQuantity(FindType(bandages, sunduk)) > 0) then
    begin
        repeat
        UseObject(FindType(bandages, sunduk));
        WaitTargetObject(self);
        Wait(11000);
        until (HP = MaxHP);
    NewMoveXY(attackx,attacky,true,0,true);
    end
else
    begin
    AddToSystemJournal('No bandages');
        repeat
        Wait(11000);
        until (GetQuantity(FindType(bandages, sunduk)) > 0);
    newMoveXY(attackx,attacky,true,0,true);
    end;
end;


procedure CheckDex;
begin
if (Dex < 130) and (GetQuantity(FindType(mfish, sunduk)) > 0) then 
    begin
    MoveItem(FindType(mfish,sunduk),1,backpack,0,0,0);
    Wait(500);
    UseObject(FindType(mfish,backpack));
    Wait(500);
    end;
end;


procedure CheckAttack;
begin
    if (WarMode = false) then SetWarMode(true);
    if GetHP(enemy) > 0 then Attack(enemy);
end;


BEGIN
InitArm;
CheckQuantites;
SetARStatus(true);
NewMoveXY(attackx,attacky,true,0,true);
while (not Dead) and Connected do
    begin
    ClearJournal;
    UseObject(sunduk);
    Hungry(1, sunduk);
    for x := 0 to 1000 do
        begin
        CheckSave;
        if EquipFullArm = 1 then CheckArm;
        if EquipWeap = 1 then CheckWeap;
        if EquipShield = 1 then CheckShield;
        if RaiseDex = 1 then CheckDex;
        if AttackEnemy = 1 then CheckAttack;
        CheckHP;
        if (HP < MaxHP / 3) then CriticalHP;
        Wait(500);
        end;
    end;
END.