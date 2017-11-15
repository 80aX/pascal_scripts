// скрипт на обнаружение разного рода опасностей. Если уровень ХП падает в 2
// или чар отравлен или в журнале появилось сообщение о краже, завёт гвардов.
// Хавает рыбу на cure если отравлен и рыбу на ХП.
// v0.1 made by 80aX for ZHR


Program guards;
{$Include 'all.inc'}

var
t : TDateTime;


procedure go_poizon;
begin
    UOSay('.guards');
    Wait(100);
    FindTypeEx($0DD6,$005B,backpack,false);
    UseObject(FindItem);
    Wait(7000);
    FindTypeEx($0DD6,$03E9,backpack,false);
    UseObject(FindItem);
    Wait(1000);
    SetARStatus(false);
    Disconnect;
end;


procedure crit_hp;
begin
    UoSay('.guards')
    Wait(100);
    FindTypeEx($0DD6,$03E9,backpack,false);
    UseObject(FindItem);
    Wait(1000);
    SetARStatus(false);
    Disconnect;
end;


procedure kill_thief;
begin
UOSay('.guards');
Wait(1000);
end;


Begin
SetARStatus(true);
while (not dead) and (connected) do
    begin
        t := now;
        if Poisoned then go_poizon;
        if (HP < MaxHP / 2) then crit_hp;
        if InJournalBetweenTimes('steal', t, now) > 0 then kill_thief;
        Wait(100);
    end;  
End.