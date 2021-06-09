<span>${field.getTitle()}</span>

<select name="_${field.getName()}_method">
    <option value="Contains">Contains</option>
    <option value="Matches">Matches</option>
    <option value=">=">&gt;=</option>
    <option value="<=">&lt;=</option>
</select>

<input type="text" name="_${field.getName()}_regex" />