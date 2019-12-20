package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.beans.support.SortDefinition;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class ShipController {

    private ShipService shipService;

    @Autowired()
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    public List<Ship> getShipList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        //filtering
        List<Ship> filteredShipsList = shipService.getFilteredShipsList(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);


        PagedListHolder<Ship> page = new PagedListHolder<>(filteredShipsList,
                new MutableSortDefinition(order.getFieldName(), false, true));
        page.resort();
        page.setPageSize(pageSize);
        page.setPage(pageNumber);

        return page.getPageList();
    }

    @GetMapping("/ships/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer countShips(            @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "planet", required = false) String planet,
                                          @RequestParam(value = "shipType", required = false) ShipType shipType,
                                          @RequestParam(value = "after", required = false) Long after,
                                          @RequestParam(value = "before", required = false) Long before,
                                          @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                          @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                          @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                          @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                          @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                          @RequestParam(value = "minRating", required = false) Double minRating,
                                          @RequestParam(value = "maxRating", required = false) Double maxRating)  {


        //filtering
        List<Ship> filteredShipsList = shipService.getFilteredShipsList(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return filteredShipsList.size();
    }

    @GetMapping(path = "/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Ship getShip(@PathVariable Long id) {
        return shipService.getShipById(id);
    }

    @DeleteMapping("/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteShip(@PathVariable("id") Long id) {
        this.shipService.deleteShip(id);
    }

    @PostMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Ship addShip(@RequestBody Ship ship) {
        return shipService.createShip(ship);
    }

    @PostMapping(value = "/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Ship editShip(@PathVariable(value = "id") Long id, @RequestBody Ship ship) {
        return shipService.updateShip(id, ship);
    }

}
