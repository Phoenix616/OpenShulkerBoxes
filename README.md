# OpenShulkerBoxes
Simple Bukkit plugin to open the inventories of Shulker Boxes without placing them down. Either via clicking in the main hand or interacting in the inventory. (Unfortunately creative mode doesn't work there)

Right click on box in inventory                             | Right click with box in hand
------------------------------------------------------------|---------------------------------------------------------
![Example inventory click](https://i.imgur.com/LQzBlVs.gif) | ![Example hotbar click](https://i.imgur.com/8gfInE2.gif)

## Command
Aliases: `/openshulkerboxes`, `/openshulker`, `/osb`

Usage: `/osb reload` - Reload the config

## Permissions:
Name                                | What it does
-------------------------------------|-------------------------------------------------
`openshulkerboxes.command`           |Gives permission to the plugin command
`openshulkerboxes.command.reload`    | Gives permission to reload the plugin
`openshulkerboxes.open.in-inventory` | Gives permission to open boxes in the inventory
`openshulkerboxes.open.from-hotbar`  | Gives permission to open boxes from the hotbar

All permissions default to `op`

## Config
```yaml
openable-items:
- "*_shulker_box"
open:
  in-inventory: right
#  in-creative: right # Creative is broken ;_;
  from-hotbar:
    action:
    - right_click_air
    - right_click_block
    sneaking: false
```

## Development builds
Development builds can be found on the [Minebench.de CI server](https://ci.minebench.de/job/OpenShulkerBoxes/) as usual.

## License

```
 OpenShulkerBoxes
 Copyright (C) 2018 Max Lee aka Phoenix616 (mail@moep.tv)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
